 ## 论坛
 
 ## 部署
 - Git
 - JDK
 - Maven
 - MySQL
 
 ## 资料
 [Spring 文档](https://spring.io/guides)  
 [Spring Web文档](https://spring.io/guides/gs/serving-web-content/)  
 [es 社区](https://elasticaearch.cn/explore)  
 [Github OAuth](https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/)
 
 
 ## 工具
 [Git](https://git-scm.com/download)   
 [Visual Paradigm](https://www.visual-paradigm.com)    
 [Flyway](https://flywaydb.org/getstarted/firststeps/maven)  
 [Lombok](https://www.projectlombok.org)    
 [ctotree](https://www.octotree.io/)   
 [Table of content sidebar](https://chrome.google.com/webstore/detail/table-of-contents-sidebar/ohohkfheangmbedkgechjkmbepeikkej)    
 [One Tab](https://chrome.google.com/webstore/detail/chphlpgkkbolifaimnlloiipkdnihall)    
 [Live Reload](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei/related)  
 [Postman](https://chrome.google.com/webstore/detail/coohjcphdfgbiolnekdpbcijmhambjff)

 ## 脚本
 ```sql
CREATE TABLE USER
(
    ID int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ACCOUNT_ID VARCHAR(100),
    NAME VARCHAR(50),
    TOKEN VARCHAR(36),
    GMT_CREATE BIGINT,
    GMT_MODIFIED BIGINT
);
```