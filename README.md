# OrderSystem
 点餐系统

该项目基于USYD_COMP9412_A1作业要求，开发一个餐厅点菜系统，其中包含两类用户类型，消费者和管理人；消费者通过该系统完成查看菜品，添加菜品，修改菜品数量，移除菜品，订单支付和历史订单查询的功能；作为管理人可以修改菜品数据包含名称，价格，描述，配料和类型，删除菜品，添加类型和订单查询以及所有消费者的操作；

该项目无使用任何GUI库，采用命令行字符的形式调用系统功能其组成如下：

| role  | command | operation               | description                                                  |
| ----- | ------- | ----------------------- | ------------------------------------------------------------ |
| Both  | menu    | show                    | Show all items on menu                                       |
| Both  | menu    | view (itemId)           | View item's detail- description, ingredient, weight, price   |
| Both  | order   | add (itemId)            | Add the specified item into the order cart                   |
| Both  | order   | show                    | Show all items on current order                              |
| Both  | order   | remove (orderItemIndex) | Remove item from order                                       |
| Both  | order   | update (orderItemIndex) | update the quantity of item                                  |
| Both  | order   | pay                     | pay order                                                    |
| Both  | history | show                    |                                                              |
| Both  | history | view (orderId)          |                                                              |
| Both  | sys     | role                    | Show current role                                            |
| Both  | sys     | login                   | login                                                        |
| Both  | sys     | help                    | printout command info                                        |
| Both  | sys     | reboot                  | reload data                                                  |
| Admin | admin   | logout                  | logout                                                       |
| Admin | admin   | dashboard               | show how many                                                |
| Admin | admin   | add                     | Admin add new category or new item                           |
| Admin | admin   | delete                  | Admin delete category or item. attention: delete category will destroy all depending items |
| Admin | admin   | update                  | Update item's name, price, description ,etc                  |
| Both  | exit    |                         | exit System                                                  |

项目实现:

购买轻量化服务器并在其中搭建MySQL数据库和Jenkins。Jenkins通过webhook实现每一次的GitHub中的分支提交后自动进行完成单元测试和代码覆盖率报告。使用Gradle实现项目的初始化和包管理。通过自定义的DAO类并引入JDBC接口完成与数据库的连接和数据操作，通过命令模式的设计模式完成对用户输入字符串的解析和过滤。引入Junit和Jacoco完成单元测试和检测代码覆盖率。
