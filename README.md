# fuchuang
服创，启动！
## 开发要求
* 前后端分别在自己的分支上开发，联调完成之前不能merge到main分支上
* 每天开发之前必须先拉一次代码！！！！！！
* 在各自的分支上先搭建好大致的框架，开发的时候再拉取新的分支做开发，分支可以用自己名字的首字母命名
  * 比如后端开发的时候，先写好需要使用的依赖和启动类，开发接口时，我拉取新的分支yr，在yr分支上做开发，自己测试完成后，再merge到backend分支上
* 每次提交必须有明确的说明，不能是“修改代码”这种笼统的话语，必须指明修改的哪里，方便之后的代码回滚和审查
* 解决冲突时，如果不是自己的修改，需要和冲突方交流后再解决，不能自己解决
## 环境配置
* 后端相关
  * 上线的服务器ip：47.109.99.11（若需要账号，请联系袁睿开通）
  * mysql：
    * 服务器：47.109.99.11  
    * 用户名：test
    * 密码：123456
  * redis：
    * 服务器：47.109.99.11
    * 端口：6379
    * 密码：123456
  * nacos：
    * 服务器：139.159.224.143
    * 端口：8848
    * ui页面
      * 地址：http://139.159.224.143:8848/nacos
      * 用户名：nacos
      * 密码：nacos
  * rocketmq：
    *  TODO: 雷宇阳写
  * 测试用户：
    * 用户名：test
    * 密码：123456
    * 验证码：002846