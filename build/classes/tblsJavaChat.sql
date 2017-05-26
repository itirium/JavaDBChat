use [JavaChat]
/*tblChatters*/
create table tblChatters (Id int not null IDENTITY(1,1) primary key,
						 UserName varchar(50) not null, 
						 UserPassword varchar(50) not null, 
						 );
go
/*tblChatServers*/
create table tblChatServers(Id int not null identity(1,1) primary key, 
					  ChatServerName varchar(100),
					  ChatServerIP varchar(12),
					  ChatServerPort int,
					  );
go
/*tblChatMessages*/
create table tblChatMessages(Id int primary key not null identity(1,1), 							
							 UserId int not null,
							 ServerId int not null,
							 MessageDT datetime not null,
							 MessageText varchar(max),
							 constraint FK_ChatMessages_UserId foreign key (UserId) 
								references tblChatters(Id)
								on delete cascade
								on update cascade,
							constraint FK_ChatMessages_ServerId foreign key (ServerId) 
								references tblChatServers(Id)
								on delete cascade
								on update cascade
								);
go


/*==============================================================================================*/
insert into tblChatServers(ChatServerName, ChatServerIp, ChatServerPort)
values (?,?,?) 