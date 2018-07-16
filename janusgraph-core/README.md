janusgraph-es： 关联反查源码修改
----------
# 验证关联反查中中文模糊匹配问题
## 数据展示
g.V(4128).both("father").valueMap(true)
==>[birthday:[Mon Jul 16 15:30:00 CST 2018],rdfs:label:[山东省济南市赵六],id:4248,name:[jupiter test],sign:[Don’t Ride Headless Horses],label:god,age:[5000]]

## 模糊匹配
g.V(4128).both("father").has("rdfs:label",Text.textContains("山东省济南市"))  //支持目标对象属性的模糊匹配

## 修改配置
解压janusgraph-es-xxx.jar包，找到配置文件`datasource.properties`。
修改其中的以下配置项：
```
cluster.httpAddresses=kg-agent-95.kg.com|kg-agent-97.kg.com|kg-server-96.kg.com
```
即配置模糊匹配的分词方式为ES，其中各ES节点Host名称之间用竖线分隔。
其他可选配置项如下：
```
#elasticsearch集群主节点http端口，可选参数
#cluster.httpPort=9200
#elaticsearch集群主节点transpotclient端口号，可选参数
#cluster.port=9300
#elasticsearch集群名称，可选参数
#cluster.name=kgCluster
```

## 其他组件
JanusGraph没有引用fastjson，而使用ES分词服务时需要依赖fastjson，因此需要把`fastjson-1.2.41.jar`手动拷贝到JanusGraph的lib文件夹下，比如`/home/janusgraph-0.2.1-hadoop2/lib`

## 原理
对源代码`org.janusgraph.core.attribute.Text`类进行了修改，将其中简单的Contains匹配方式中的分词替换为ES分词，调用的ES Restful分词服务。为保证匹配正确性，该ES服务应该与JanusGraph索引后端Elasticsearch统一。

## 注意
以上仅限于在gremlin console中使用。当应用于项目时，直接在项目`datasource.properties`配置ES Host即可。