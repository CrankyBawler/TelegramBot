-- liquebase formatted sql

--changeset lBorisov:1
create schema telegramm_bot;
--changeset lBorisov:2
create table notification_task
(
    id bigSerial primary key ,
    user_id bigInt not null,
    text text not null ,
    date timestamp not null
);