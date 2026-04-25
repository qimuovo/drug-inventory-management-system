# 药品库存管理系统

## 在线演示

在线演示：http://drug.qimuovo.cn

登录账号：admin —— 123456



## Docker 一键启动

在项目根目录执行：

```bash
docker compose up -d --build
```

启动后访问：

- 前端：http://ip
- 后端接口：http://ip:8080/api



## 本地运行

- 后端：修改 application.yaml 中的 mysql 配置

  导入mysql库表：/sql目录下的create.sql 和 data.sql

  运行 DrugInventoryApplication

- 前端：`npm install`  

   `npm run dev`



