--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

DROP DATABASE telesto;
--
-- Name: telesto; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE telesto WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'German_Switzerland.1252' LC_CTYPE = 'German_Switzerland.1252';


\connect telesto

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: create_queue(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION create_queue(p_queue_name character varying) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    INSERT INTO queues (queue_id, queue_name) VALUES (DEFAULT, p_queue_name) RETURNING queue_id, queue_name;
$$;


--
-- Name: delete_client(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION delete_client(p_client_id integer) RETURNS integer
    LANGUAGE sql
    AS $$  
    DELETE FROM clients WHERE client_id = p_client_id RETURNING client_id
$$;


--
-- Name: delete_queue(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION delete_queue(p_queue_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$  
DECLARE 
	d_queue_id integer;
BEGIN
    DELETE FROM queues WHERE queue_id = p_queue_id RETURNING queue_id INTO d_queue_id;
    DELETE FROM messages WHERE queue_id = p_queue_id;
    RETURN d_queue_id;
END
$$;


--
-- Name: get_active_queues(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_active_queues(p_client_id integer) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT q.queue_id, q.queue_name FROM messages m JOIN queues q ON q.queue_id = m.queue_id WHERE coalesce(m.receiver_id, p_client_id) = p_client_id;
$$;


--
-- Name: get_messages_from_queue(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_messages_from_queue(p_queue_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
    SELECT m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message FROM messages m WHERE m.queue_id = p_queue_id;
$$;


--
-- Name: get_queue_id(character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_queue_id(p_queue_name character varying) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues WHERE queue_name = p_queue_name;
$$;


--
-- Name: get_queue_name(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION get_queue_name(p_queue_id integer) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues WHERE queue_id = p_queue_id;
$$;


--
-- Name: identify(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION identify(p_client_id integer) RETURNS TABLE(client_id integer, client_name character varying, operation_mode smallint)
    LANGUAGE sql
    AS $$   
    SELECT c.client_id, c.client_name, c.operation_mode FROM clients c WHERE c.client_id = p_client_id;
$$;


--
-- Name: list_queues(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION list_queues() RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues;
$$;


--
-- Name: put_message(integer, integer, integer, integer, smallint, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION put_message(p_queue_id integer, p_sender_id integer, p_receiver_id integer, p_context integer, p_priority smallint, p_message character varying) RETURNS integer
    LANGUAGE sql
    AS $$
 
INSERT INTO messages ( 
	queue_id, 
	sender_id, 
	receiver_id, 
	context, 
	priority, 
	message
) 
SELECT 
	p_queue_id, 
	p_sender_id, 
	p_receiver_id, 
	p_context, 
	p_priority, 
	p_message
WHERE EXISTS (
	SELECT queue_id FROM queues q WHERE q.queue_id = p_queue_id
) RETURNING messages.queue_id;

$$;


--
-- Name: put_messages(integer[], integer, integer, integer, smallint, character varying); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION put_messages(p_queue_ids integer[], p_sender_id integer, p_receiver_id integer, p_context integer, p_priority smallint, p_message character varying) RETURNS TABLE(queue_id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
	p_queue_id integer;
BEGIN
	FOREACH p_queue_id IN ARRAY p_queue_ids
	LOOP 
	    RETURN QUERY INSERT INTO messages ( 
			queue_id, 
			sender_id, 
			receiver_id, 
			context, 
			priority, 
			message
		) 
		SELECT 
			p_queue_id, 
			p_sender_id, 
			p_receiver_id, 
			p_context, 
			p_priority, 
			p_message
		WHERE EXISTS (
			SELECT q.queue_id FROM queues q WHERE q.queue_id = p_queue_id
		) RETURNING messages.queue_id;
	END LOOP;
END
$$;


--
-- Name: read_message_by_priority(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION read_message_by_priority(p_queue_id integer, p_sender_id integer, p_receiver_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = p_queue_id 
		AND 	coalesce(p_sender_id, m.sender_id) = m.sender_id 
		AND 	coalesce(m.receiver_id, p_receiver_id) = p_receiver_id 
		ORDER BY m.priority DESC 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


--
-- Name: read_message_by_timestamp(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION read_message_by_timestamp(p_queue_id integer, p_sender_id integer, p_receiver_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = p_queue_id 
		AND 	coalesce(p_sender_id, m.sender_id) = m.sender_id 
		AND 	coalesce(m.receiver_id, p_receiver_id) = p_receiver_id 
		ORDER BY m.time_of_arrival DESC 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


--
-- Name: read_response_message(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION read_response_message(p_queue_id integer, p_receiver_id integer, p_context integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = p_queue_id 
		AND 	m.receiver_id = p_receiver_id 
		AND     m.context = p_context 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


--
-- Name: request_id(character varying, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION request_id(p_client_name character varying, p_operation_mode integer) RETURNS integer
    LANGUAGE sql
    AS $$INSERT INTO clients (
		client_id, 
		client_name, 
		operation_mode
	) VALUES (
		DEFAULT, 
		p_client_name, 
		p_operation_mode
	) RETURNING client_id;
$$;


SET default_with_oids = false;

--
-- Name: clients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clients (
    client_id integer NOT NULL,
    client_name character varying(255),
    operation_mode smallint
);


--
-- Name: messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE messages (
    message_id integer NOT NULL,
    queue_id integer,
    sender_id integer NOT NULL,
    receiver_id integer,
    context integer,
    priority smallint,
    time_of_arrival timestamp without time zone DEFAULT now(),
    message character varying(2000)
);


--
-- Name: messages_message_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE messages_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messages_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE messages_message_id_seq OWNED BY messages.message_id;


--
-- Name: queues; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE queues (
    queue_id integer NOT NULL,
    queue_name character varying(255)
);


--
-- Name: queue_queue_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE queue_queue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: queue_queue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE queue_queue_id_seq OWNED BY queues.queue_id;


--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE users_user_id_seq OWNED BY clients.client_id;


--
-- Name: client_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY clients ALTER COLUMN client_id SET DEFAULT nextval('users_user_id_seq'::regclass);


--
-- Name: message_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY messages ALTER COLUMN message_id SET DEFAULT nextval('messages_message_id_seq'::regclass);


--
-- Name: queue_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY queues ALTER COLUMN queue_id SET DEFAULT nextval('queue_queue_id_seq'::regclass);


--
-- Name: pk_client_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT pk_client_id PRIMARY KEY (client_id);


--
-- Name: pk_message_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT pk_message_id PRIMARY KEY (message_id);


--
-- Name: pk_queue_id; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY queues
    ADD CONSTRAINT pk_queue_id PRIMARY KEY (queue_id);


--
-- Name: unique_client_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT unique_client_name UNIQUE (client_name);


--
-- Name: unique_queue_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY queues
    ADD CONSTRAINT unique_queue_name UNIQUE (queue_name);


--
-- Name: idx_receiver_queue_priority; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_receiver_queue_priority ON messages USING btree (receiver_id, queue_id, priority DESC);


--
-- Name: idx_receiver_queue_priority_sender; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_receiver_queue_priority_sender ON messages USING btree (receiver_id, queue_id, priority DESC, sender_id);


--
-- Name: idx_receiver_queue_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_receiver_queue_time ON messages USING btree (receiver_id, queue_id, time_of_arrival);


--
-- Name: idx_receiver_queue_time_sender; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_receiver_queue_time_sender ON messages USING btree (receiver_id, queue_id, time_of_arrival, sender_id);


--
-- PostgreSQL database dump complete
--

