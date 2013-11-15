--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.1
-- Dumped by pg_dump version 9.3.1
-- Started on 2013-11-14 01:30:35

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

DROP DATABASE telesto;
--
-- TOC entry 1981 (class 1262 OID 16394)
-- Name: telesto; Type: DATABASE; Schema: -; Owner: telesto
--

CREATE DATABASE telesto WITH TEMPLATE = template0 ENCODING = 'UTF8';


ALTER DATABASE telesto OWNER TO telesto;

\connect telesto

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: telesto
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO telesto;

--
-- TOC entry 1982 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: telesto
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 176 (class 3079 OID 11750)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1984 (class 0 OID 0)
-- Dependencies: 176
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 197 (class 1255 OID 16467)
-- Name: create_queue(character varying); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION create_queue(p_queue_name character varying) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    INSERT INTO queues (queue_id, queue_name) VALUES (DEFAULT, $1) RETURNING queue_id, $1;
$$;


ALTER FUNCTION public.create_queue(p_queue_name character varying) OWNER TO telesto;

--
-- TOC entry 199 (class 1255 OID 17028)
-- Name: delete_client(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION delete_client(p_client_id integer) RETURNS integer
    LANGUAGE sql
    AS $$  
    DELETE FROM clients WHERE client_id = $1 RETURNING client_id
$$;


ALTER FUNCTION public.delete_client(p_client_id integer) OWNER TO telesto;

--
-- TOC entry 193 (class 1255 OID 17026)
-- Name: delete_queue(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION delete_queue(p_queue_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$  
DECLARE 
	d_queue_id integer;
BEGIN
    DELETE FROM queues WHERE queue_id = $1 RETURNING queue_id INTO d_queue_id;
    DELETE FROM messages WHERE queue_id = $1;
    RETURN d_queue_id;
END
$$;


ALTER FUNCTION public.delete_queue(p_queue_id integer) OWNER TO telesto;

--
-- TOC entry 195 (class 1255 OID 16476)
-- Name: get_active_queues(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION get_active_queues(p_client_id integer) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT q.queue_id, q.queue_name FROM messages m JOIN queues q ON q.queue_id = m.queue_id WHERE coalesce(m.receiver_id, $1) = $1;
$$;


ALTER FUNCTION public.get_active_queues(p_client_id integer) OWNER TO telesto;

--
-- TOC entry 196 (class 1255 OID 16477)
-- Name: get_messages_from_queue(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION get_messages_from_queue(p_queue_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
    SELECT m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message FROM messages m WHERE m.queue_id = $1;
$$;


ALTER FUNCTION public.get_messages_from_queue(p_queue_id integer) OWNER TO telesto;

--
-- TOC entry 189 (class 1255 OID 16469)
-- Name: get_queue_id(character varying); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION get_queue_id(p_queue_name character varying) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues WHERE queue_name = $1;
$$;


ALTER FUNCTION public.get_queue_id(p_queue_name character varying) OWNER TO telesto;

--
-- TOC entry 191 (class 1255 OID 16471)
-- Name: get_queue_name(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION get_queue_name(p_queue_id integer) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues WHERE queue_id = $1;
$$;


ALTER FUNCTION public.get_queue_name(p_queue_id integer) OWNER TO telesto;

--
-- TOC entry 192 (class 1255 OID 16464)
-- Name: identify(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION identify(p_client_id integer) RETURNS TABLE(client_id integer, client_name character varying, operation_mode smallint)
    LANGUAGE sql
    AS $$   
    SELECT c.client_id, c.client_name, c.operation_mode FROM clients c WHERE c.client_id = $1;
$$;


ALTER FUNCTION public.identify(p_client_id integer) OWNER TO telesto;

--
-- TOC entry 194 (class 1255 OID 16472)
-- Name: list_queues(); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION list_queues() RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    SELECT queue_id, queue_name FROM queues;
$$;


ALTER FUNCTION public.list_queues() OWNER TO telesto;

--
-- TOC entry 203 (class 1255 OID 16488)
-- Name: put_message(integer, integer, integer, integer, smallint, character varying); Type: FUNCTION; Schema: public; Owner: telesto
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
	$1, 
	$2, 
	$3, 
	$4, 
	$5, 
	$6
WHERE EXISTS (
	SELECT queue_id FROM queues q WHERE q.queue_id = $1
) RETURNING messages.queue_id;

$$;


ALTER FUNCTION public.put_message(p_queue_id integer, p_sender_id integer, p_receiver_id integer, p_context integer, p_priority smallint, p_message character varying) OWNER TO telesto;

--
-- TOC entry 202 (class 1255 OID 16489)
-- Name: put_messages(integer[], integer, integer, integer, smallint, character varying); Type: FUNCTION; Schema: public; Owner: telesto
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


ALTER FUNCTION public.put_messages(p_queue_ids integer[], p_sender_id integer, p_receiver_id integer, p_context integer, p_priority smallint, p_message character varying) OWNER TO telesto;

--
-- TOC entry 198 (class 1255 OID 16482)
-- Name: read_message_by_priority(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION read_message_by_priority(p_queue_id integer, p_sender_id integer, p_receiver_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = $1 
		AND 	coalesce($2, m.sender_id) = m.sender_id 
		AND 	coalesce(m.receiver_id, $3) = $3 
		ORDER BY m.priority DESC 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


ALTER FUNCTION public.read_message_by_priority(p_queue_id integer, p_sender_id integer, p_receiver_id integer) OWNER TO telesto;

--
-- TOC entry 200 (class 1255 OID 16483)
-- Name: read_message_by_timestamp(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION read_message_by_timestamp(p_queue_id integer, p_sender_id integer, p_receiver_id integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = $1 
		AND 	coalesce($2, m.sender_id) = m.sender_id 
		AND 	coalesce(m.receiver_id, $3) = $3 
		ORDER BY m.time_of_arrival DESC 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


ALTER FUNCTION public.read_message_by_timestamp(p_queue_id integer, p_sender_id integer, p_receiver_id integer) OWNER TO telesto;

--
-- TOC entry 201 (class 1255 OID 17323)
-- Name: read_response_message(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION read_response_message(p_queue_id integer, p_receiver_id integer, p_context integer) RETURNS TABLE(message_id integer, queue_id integer, sender_id integer, receiver_id integer, context integer, priority smallint, time_of_arrival timestamp without time zone, message character varying)
    LANGUAGE sql
    AS $$  
	DELETE FROM messages m WHERE m.message_id = (
		SELECT m.message_id FROM messages m 
		WHERE 	m.queue_id = $1 
		AND 	m.receiver_id = $2 
		AND     m.context = $3 
		LIMIT 1
	) RETURNING m.message_id, m.queue_id, m.sender_id, m.receiver_id, m.context, m.priority, m.time_of_arrival, m.message;
$$;


ALTER FUNCTION public.read_response_message(p_queue_id integer, p_receiver_id integer, p_context integer) OWNER TO telesto;

--
-- TOC entry 190 (class 1255 OID 16461)
-- Name: request_id(character varying, integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION request_id(p_client_name character varying, p_operation_mode integer) RETURNS integer
    LANGUAGE sql
    AS $$INSERT INTO clients (
		client_id, 
		client_name, 
		operation_mode
	) VALUES (
		DEFAULT, 
		$1, 
		$2
	) RETURNING client_id;
$$;


ALTER FUNCTION public.request_id(p_client_name character varying, p_operation_mode integer) OWNER TO telesto;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 175 (class 1259 OID 16417)
-- Name: clients; Type: TABLE; Schema: public; Owner: telesto; Tablespace: 
--

CREATE TABLE clients (
    client_id integer NOT NULL,
    client_name character varying(255),
    operation_mode smallint
);


ALTER TABLE public.clients OWNER TO telesto;

--
-- TOC entry 174 (class 1259 OID 16415)
-- Name: clients_client_id_seq; Type: SEQUENCE; Schema: public; Owner: telesto
--

CREATE SEQUENCE clients_client_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.clients_client_id_seq OWNER TO telesto;

--
-- TOC entry 1986 (class 0 OID 0)
-- Dependencies: 174
-- Name: clients_client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: telesto
--

ALTER SEQUENCE clients_client_id_seq OWNED BY clients.client_id;


--
-- TOC entry 173 (class 1259 OID 16406)
-- Name: messages; Type: TABLE; Schema: public; Owner: telesto; Tablespace: 
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


ALTER TABLE public.messages OWNER TO telesto;

--
-- TOC entry 172 (class 1259 OID 16404)
-- Name: messages_message_id_seq; Type: SEQUENCE; Schema: public; Owner: telesto
--

CREATE SEQUENCE messages_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_message_id_seq OWNER TO telesto;

--
-- TOC entry 1988 (class 0 OID 0)
-- Dependencies: 172
-- Name: messages_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: telesto
--

ALTER SEQUENCE messages_message_id_seq OWNED BY messages.message_id;


--
-- TOC entry 171 (class 1259 OID 16398)
-- Name: queues; Type: TABLE; Schema: public; Owner: telesto; Tablespace: 
--

CREATE TABLE queues (
    queue_id integer NOT NULL,
    queue_name character varying(255)
);


ALTER TABLE public.queues OWNER TO telesto;

--
-- TOC entry 170 (class 1259 OID 16396)
-- Name: queue_queue_id_seq; Type: SEQUENCE; Schema: public; Owner: telesto
--

CREATE SEQUENCE queue_queue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.queue_queue_id_seq OWNER TO telesto;

--
-- TOC entry 1990 (class 0 OID 0)
-- Dependencies: 170
-- Name: queue_queue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: telesto
--

ALTER SEQUENCE queue_queue_id_seq OWNED BY queues.queue_id;


--
-- TOC entry 1855 (class 2604 OID 17341)
-- Name: client_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY clients ALTER COLUMN client_id SET DEFAULT nextval('clients_client_id_seq'::regclass);


--
-- TOC entry 1853 (class 2604 OID 16409)
-- Name: message_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY messages ALTER COLUMN message_id SET DEFAULT nextval('messages_message_id_seq'::regclass);


--
-- TOC entry 1852 (class 2604 OID 16401)
-- Name: queue_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY queues ALTER COLUMN queue_id SET DEFAULT nextval('queue_queue_id_seq'::regclass);


--
-- TOC entry 1867 (class 2606 OID 16422)
-- Name: pk_client_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT pk_client_id PRIMARY KEY (client_id);


--
-- TOC entry 1865 (class 2606 OID 16414)
-- Name: pk_message_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT pk_message_id PRIMARY KEY (message_id);


--
-- TOC entry 1857 (class 2606 OID 16424)
-- Name: pk_queue_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY queues
    ADD CONSTRAINT pk_queue_id PRIMARY KEY (queue_id);


--
-- TOC entry 1869 (class 2606 OID 16498)
-- Name: unique_client_name; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT unique_client_name UNIQUE (client_name);


--
-- TOC entry 1859 (class 2606 OID 16493)
-- Name: unique_queue_name; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY queues
    ADD CONSTRAINT unique_queue_name UNIQUE (queue_name);


--
-- TOC entry 1860 (class 1259 OID 16428)
-- Name: idx_receiver_queue_priority; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_priority ON messages USING btree (receiver_id, queue_id, priority DESC);


--
-- TOC entry 1861 (class 1259 OID 16427)
-- Name: idx_receiver_queue_priority_sender; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_priority_sender ON messages USING btree (receiver_id, queue_id, priority DESC, sender_id);


--
-- TOC entry 1862 (class 1259 OID 16431)
-- Name: idx_receiver_queue_time; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_time ON messages USING btree (receiver_id, queue_id, time_of_arrival);


--
-- TOC entry 1863 (class 1259 OID 16432)
-- Name: idx_receiver_queue_time_sender; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_time_sender ON messages USING btree (receiver_id, queue_id, time_of_arrival, sender_id);


--
-- TOC entry 1983 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: telesto
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM telesto;
GRANT ALL ON SCHEMA public TO telesto;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 1985 (class 0 OID 0)
-- Dependencies: 175
-- Name: clients; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE clients FROM PUBLIC;
REVOKE ALL ON TABLE clients FROM telesto;
GRANT ALL ON TABLE clients TO telesto;
GRANT ALL ON TABLE clients TO PUBLIC;


--
-- TOC entry 1987 (class 0 OID 0)
-- Dependencies: 173
-- Name: messages; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE messages FROM PUBLIC;
REVOKE ALL ON TABLE messages FROM telesto;
GRANT ALL ON TABLE messages TO telesto;
GRANT ALL ON TABLE messages TO PUBLIC;


--
-- TOC entry 1989 (class 0 OID 0)
-- Dependencies: 171
-- Name: queues; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE queues FROM PUBLIC;
REVOKE ALL ON TABLE queues FROM telesto;
GRANT ALL ON TABLE queues TO telesto;
GRANT ALL ON TABLE queues TO PUBLIC;


--
-- TOC entry 1520 (class 826 OID 16395)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO PUBLIC;


-- Completed on 2013-11-14 01:30:35

--
-- PostgreSQL database dump complete
--

