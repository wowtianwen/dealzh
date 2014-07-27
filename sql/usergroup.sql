
-- Dumping data for table forum1.user: ~11 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
REPLACE INTO `user` (`id`, `createTime`, `version`, `NumOfReplies`, `currentTitle`, `email`, `firstName`, `isCategoryAdmin`, `isOnline`, `lastLogin`, `lastName`, `locked`, `middleName`, `numOfPosts`, `password`, `passwordagain`, `phoneNumber`, `profilePicName`, `reputation`, `status`, `title`, `userId`, `usergroup_Id`) VALUES
	(1, '2012-07-03 23:43:25', 367, 46, NULL, 'zhang.j.andrew@gmail.com', NULL, b'00000000', b'10000000', '2012-10-04', NULL, b'00000000', NULL, 192, 'd914e3ecf6cc481114a3f534a5faf90b', 'd914e3ecf6cc481114a3f534a5faf90b', NULL, 'sysadmin.jpg', 0, b'00000000', NULL, 'sysadmin', 4),
	(2, '2012-07-04 10:10:02', 121, 22, NULL, 'zhang.j.andrew@gmail.com', NULL, b'00000000', b'00000000', '2012-10-04', NULL, b'00000000', NULL, 70, 'd914e3ecf6cc481114a3f534a5faf90b', 'd914e3ecf6cc481114a3f534a5faf90b', NULL, 'azhang.jpg', 0, b'00000000', NULL, 'azhang', 2),


/*!40000 ALTER TABLE `user` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


desc usergroup

insert into usergroup values(1,'2011-08-17','1','GUEST','GUEST');
insert into usergroup values(2,'2011-08-17','1','REGISTERED','REGISTERED');
insert into usergroup values(3,'2011-08-17','1','CAEGORY_OWNER','CAEGORY_OWNER');
insert into usergroup values(4,'2011-08-17','1','SYSADMIN','SYSADMIN');
insert into usergroup values(5,'2011-08-17','1','NEWSPOSTER','NEWSPOSTER');


update user set usergroup_id=4 where id=1

select * from usergroup;
select * from user
select * from category
select * from thread;
select * from reply;
select * from notification;

select * from user_sticky_thread;

delete from user_sticky_thread;
update user set usergroup_id=4 where id=1

select * from thread;

delete from user where id=2;

drop database forum1;
desc usergroup

select forumthread from thread forumthrea0_ where forumthrea0_.category_id=? order by forumthrea0_.createTime desc limit ?
select * from category forumcateg0_ left outer join category childrenca1_ on forumcateg0_.id=childrenca1_.parentCategory_Id left outer join User user2_ on childrenca1_.owner_id=user2_.id left outer join category forumcateg3_ on childrenca1_.parentCategory_id=forumcateg3_.id where forumcateg0_.id=?
 select * from User user0_ left outer join UserGroup usergroup1_ on user0_.usergroup_Id=usergroup1_.id where user0_.id=?

select * from thread where 


select * from reply where thread_Id=14


 select
        count(notificati0_.id) as col_0_0_ 
    from
        notification notificati0_ 
    inner join
        notification_User to1_ 
            on notificati0_.id=to1_.notification_id 
    inner join
        User user2_ 
            on to1_.to_id=user2_.id 
    where
        notificati0_.status=0
        and (
            1 in (
                select
                    user2_.id 
                from
                    User user2_
            )
        ) limit ?
        
        
        
    desc notification_user;    
    select * from notification_user;
        
        
        
        select n.* from notification n , notification_user u where n.id=u.notification_id and n.from_id=1 and u.to_id=10
        
        
        
        
        
        union 
        
        select n.* from notification n , notification_user u where n.id=u.notification_id and n.from_id=:toUserId and u.to_id=:currUserId
