package org.fuchuang.biz.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.userservice.common.constant.RedisKeyConstant;
import org.fuchuang.biz.userservice.common.constant.UserConstant;
import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dao.mapper.UserMapper;
import org.fuchuang.biz.userservice.dto.req.UserForgetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserPersonalInfoRespDTO;
import org.fuchuang.biz.userservice.service.UserInfoService;
import org.fuchuang.framework.starter.cache.DistributedCache;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserInfoService {

    private final UserMapper userMapper;

    private final DistributedCache distributedCache;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用户密码重置
     * @param requestParam 用户密码重置请求参数
     */
    @Override
    public void resetPassword(UserResetPasswordReqDTO requestParam) {
        // 参数校验
        if(requestParam == null || StrUtil.isBlank(requestParam.getOldPassword()) || StrUtil.isBlank(requestParam.getNewPassword())){
            throw new ClientException("参数不能为空！");
        }

        // 验证旧密码
        // 从用户上下文中获取用户id，并在数据库中查询用户信息(redis中的没有密码)
//        log.info("上下文获取到的userId：{}", UserContext.getUserId());
//        Long userId = Long.valueOf(UserContext.getUserId());
        String userId = requestParam.getUserId();
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null) {
            throw new ClientException("用户不存在！");
        }
        String oldPassword = requestParam.getOldPassword();
        String oldPasswordWithMD5 = DigestUtil.md5Hex(oldPassword + userDO.getSalt());
        if (!oldPasswordWithMD5.equals(userDO.getPassword())) {
            throw new ClientException("旧密码错误！");
        }

        // 防御性编程，校验新旧密码是否一致
        String newPassword = requestParam.getNewPassword();
        if (oldPassword.equals(newPassword)) {
            throw new ClientException("新密码不能与旧密码相同！");
        }

        // 验证新密码是否合法
        if (newPassword.length() > UserConstant.PASSWORD_MAX_LENGTH || newPassword.length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_ILLEGAL);
        }

        // 更新密码
        updatePassword(newPassword, userDO);
    }

    /**
     * 提取出来的密码更新方法
     * @param newPassword 新密码
     * @param userDO 用户实体，用于获取salt
     */
    private void updatePassword(String newPassword, UserDO userDO) {
        String newPasswordWithMD5 = DigestUtil.md5Hex(newPassword + userDO.getSalt());
        UserDO updateUser = UserDO.builder()
                .id(userDO.getId())
                .password(newPasswordWithMD5)
                .build();
        try {
            userMapper.updateById(updateUser);
        }catch (Exception e) {
            log.error("用户密码更新失败：{}", e.getMessage());
            throw new ServiceException("密码更新失败！");
        }
    }

    /**
     * 用户忘记密码
     * @param requestParam 忘记密码请求参数
     */
    @Override
    public void forgetPassword(UserForgetPasswordReqDTO requestParam) {
        // 参数校验
        if(requestParam == null || StrUtil.isBlank(requestParam.getEmail()) || StrUtil.isBlank(requestParam.getCode()) ||
                StrUtil.isBlank(requestParam.getNewPassword())){
            throw new ClientException("参数不能为空！");
        }

        String newPassword = requestParam.getNewPassword();
        // 验证新密码是否合法
        if (newPassword.length() > UserConstant.PASSWORD_MAX_LENGTH || newPassword.length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_ILLEGAL);
        }

        // 验证验证码
        // 从redis中获取验证码
        String codeKey = RedisKeyConstant.USER_FORGET_PASSWORD_VERIFY_CODE + requestParam.getEmail();
        StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
        String code = stringRedisTemplate.opsForValue().get(codeKey);
        if (StrUtil.isBlank(code)) {
            throw new ClientException("验证码已过期！");
        }
        if (!code.equals(requestParam.getCode())) {
            throw new ClientException("验证码错误！");
        }

        // 更新密码
        // 从数据库中获取用户信息
        UserDO userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getEmail, requestParam.getEmail()));
        // 更新密码
        updatePassword(newPassword, userDO);
    }

    /**
     * 用户头像上传
     * @param imageFile 用户头像文件
     */
    @Override
    public void uploadUserImage(MultipartFile imageFile) {
        //1. 获取文件名
        //1.1 校验文件是否为空
        if (imageFile == null){
            throw new ClientException("头像不能为空！");
        }
        String fileName = imageFile.getOriginalFilename();
        if (fileName == null){
            throw new ClientException("不支持的文件类型！");
        }
        //2. 分别获取文件后缀与文件名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //2.1 检查是否是想要的文件类型
        if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) ){
            throw new ClientException("不支持的文件类型！");
        }
        //2.2 拼接文件名
        String name = UUID.randomUUID() + suffix;
        //3. 上传文件
        String url = null;
        try {
            // TODO 实现头像上传到oss逻辑
        } catch (Exception e) {
            log.error("上传头像失败: {}", e.getMessage());
        }
    }

    /**
     * 获取用户个人信息
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public UserPersonalInfoRespDTO getUserPersonalInfo(Long userId) {
        //  获取到用户的id,如果为空,则说明是查自己，从ThreadLocal中获取
        if (userId == null){
            userId = Long.valueOf(UserContext.getUserId());
        }
        log.info("用户个人信息查询: {}", userId);

        // 从 redis 中获取用户信息
        String userInfoRedis = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_INFO_KEY + userId);
        if (userInfoRedis != null){
            // redis 中存在用户信息，直接返回
            log.info("从redis获取的用户信息：{}", userInfoRedis);
            return JSON.parseObject(userInfoRedis, UserPersonalInfoRespDTO.class);
        }
        //  根据用户id查询用户信息, 只需要 用户名、头像、签名
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username", "image", "signature").eq("id", userId);
        UserDO user = userMapper.selectOne(queryWrapper);
        // 校验用户信息是否为空
        if (user == null){
            throw new ClientException("用户不存在！");
        }
        log.info("从mysql获取的用户信息：{}", user);
        // 拷贝属性
        UserPersonalInfoRespDTO userPersonalInfoRespDTO = new UserPersonalInfoRespDTO();
        BeanUtils.copyProperties(user, userPersonalInfoRespDTO);
        userPersonalInfoRespDTO.setUserId(String.valueOf(userId));
        // 将用户信息存入 redis
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER_INFO_KEY + userId, JSON.toJSONString(userPersonalInfoRespDTO),
                RedisKeyConstant.REDIS_USER_INFO_TTL, TimeUnit.SECONDS);
        // 返回结果
        return userPersonalInfoRespDTO;
    }

    /**
     * 用户信息修改
     * @param requestParam 用户修改信息参数
     */
    @Override
    public void userInfoUpdate(UserResetReqDTO requestParam) {
        log.info("传入用户信息修改参数为：{}", requestParam);

        // 校验参数
        if (requestParam == null || StrUtil.isBlank(requestParam.getEmail()) ||
                StrUtil.isBlank(requestParam.getUsername())) {
            throw new ClientException("参数不能为空！");
        }

        // 1 获取用户信息判断是否登录
        String userId = requestParam.getUserId();

        // 1.2 校验用户id是否为空
        if (StrUtil.isBlank(userId)) {
            throw new ClientException("请先登录！");
        }
        // 1.3 检验用户id是否存在
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new ClientException("用户不存在！");
        }

        // 2 校验修改信息是否合法
        // 2.1 用户名长度
        if (requestParam.getUsername().length() > UserConstant.PASSWORD_MAX_LENGTH || requestParam.getUsername().length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new ClientException("用户名要在5~20位之间！");
        }
        // 2.2 用户签名长度
        if (requestParam.getSignature().length() > UserConstant.SIGNATURE_MAX_LENGTH) {
            throw new ClientException("用户签名长度不能超过100个字符！");
        }

        // 旁路缓存策略实现数据更新
        // 删除redis中的用户信息
        distributedCache.delete(RedisKeyConstant.USER_INFO_KEY + userId);

        // 更新数据库
        UserDO updateUser = UserDO.builder()
                .email(requestParam.getEmail())
                .username(requestParam.getUsername())
                .signature(requestParam.getSignature())
                .build();
        updateUser.setId(Long.valueOf(userId));
        try {
            userMapper.updateById(updateUser);
        } catch (Exception e) {
            log.error("用户信息更新失败：{}", e.getMessage());
            throw new ClientException("个人信息更新失败！");
        }
    }
}
