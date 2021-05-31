## 分布式锁实现

1. 基于MySQL实现

```sql
create table if not exists d_locked
(
    id           int unsigned auto_increment,
    locked_key     varchar(255) not null default '' comment 'lock identity key',
    created_date datetime     not null default current_timestamp comment 'created time',

    primary key (id),
    unique key (lock_key)
) engine = innodb charset utf8mb4 comment 'distribute lock table.'

```

原理就是利用MySQL的innodb行级悲观锁：（select * from d_locked where xxx for update）来锁住单行记录。其它事务没办法拿到锁，一直等待。


