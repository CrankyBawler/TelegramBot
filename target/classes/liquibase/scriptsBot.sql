-- liquebase formatted sql


--changeset lBorisov:1
CREATE TABLE notification_task (
                                   id SERIAL NOT NULL,
                                   chat_id SERIAL NOT NULL,
                                   tast_text TEXT NOT NULL,
                                   perform_date TIMESTAMP NOT NULL
);