use [JavaChat]
/*tblChatUsers*/
create table tblChatUsers (Id int not null IDENTITY(1,1) primary key,
						 UserName varchar(50) not null, 
						 UserPassword varchar(50) not null,
						 UserSelectedColor int 
						 );
go
/*tblChatServers*/
create table tblChatServers(Id int not null identity(1,1) primary key, 
					  ChatServerName varchar(100),
					  ChatServerIP varchar(20),
					  ChatServerPort int,
					  );
go
/*tblChatServersMessages*/
create table tblChatServersMessages(Id int not null identity(1,1) primary key, 
					  ChatServerId int not null,
					  MessageDT datetime not null,
					  ChatServerMessage varchar(max)
					  constraint FK_ChatServersMessages_ServerId foreign key (ChatServerId) 
								references tblChatServers(Id)
								on delete cascade
								on update cascade,
					  );
go
/*tblChatMessages*/
create table tblChatMessages(Id int primary key not null identity(1,1), 							
							 UserId int not null,
							 ServerId int not null,
							 MessageDT datetime not null,
							 MessageText varchar(max),
							 constraint FK_ChatMessages_UserId foreign key (UserId) 
								references tblChatUsers(Id)
								on delete cascade
								on update cascade,
							constraint FK_ChatMessages_ServerId foreign key (ServerId) 
								references tblChatServers(Id)
								on delete cascade
								on update cascade
								);
go


