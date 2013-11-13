--
-- PostgreSQL database script
--

-- Cleanup all tables and reset sequences


\connect telesto

TRUNCATE clients, queues, messages;

ALTER SEQUENCE clients_client_id_seq RESTART;
ALTER SEQUENCE queue_queue_id_seq RESTART;
ALTER SEQUENCE messages_message_id_seq RESTART;