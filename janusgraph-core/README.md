janusgraph-es： 关联反查源码修改
----------
# 验证关联反查中中文模糊匹配问题
## 数据展示
g.V(4128).both("father").valueMap(true)
==>[birthday:[Mon Jul 16 15:30:00 CST 2018],rdfs:label:[山东省济南市赵六],id:4248,name:[jupiter test],sign:[Don’t Ride Headless Horses],label:god,age:[5000]]

## 模糊匹配
g.V(4128).both("father").has("rdfs:label",Text.textContains("山东省济南市"))  //支持目标对象属性的模糊匹配

## 原理
对源代码`org.janusgraph.core.attribute.Text`类进行了修改，将其中简单的Contains匹配方式中的分词替换为ES分词，调用的IK 分词服务。
可以在main.dic与stopword-HIT.dic中配置用户词典与停用词典。
