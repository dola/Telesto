--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.1
-- Dumped by pg_dump version 9.3.1
-- Started on 2013-10-25 01:49:32

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


--
-- Create User
-- Name: telesto; Password: blubbi
--

CREATE ROLE telesto LOGIN ENCRYPTED PASSWORD 'md58c65d72f40d9b73c6049c4b87136975a'
  CREATEDB
   VALID UNTIL 'infinity';


--
-- TOC entry 1970 (class 1262 OID 16394)
-- Name: telesto; Type: DATABASE; Schema: -; Owner: telesto
--

CREATE DATABASE telesto WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'German_Switzerland.1252' LC_CTYPE = 'German_Switzerland.1252';


ALTER DATABASE telesto OWNER TO telesto;

\connect telesto

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 176 (class 3079 OID 11750)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1973 (class 0 OID 0)
-- Dependencies: 176
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 191 (class 1255 OID 16467)
-- Name: create_queue(character varying); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION create_queue(p_queue_name character varying) RETURNS TABLE(queue_id integer, queue_name character varying)
    LANGUAGE sql
    AS $$  
    INSERT INTO queues (queue_id, queue_name) VALUES (DEFAULT, p_queue_name) RETURNING queue_id, queue_name;
$$;


ALTER FUNCTION public.create_queue(p_queue_name character varying) OWNER TO telesto;

--
-- TOC entry 190 (class 1255 OID 16464)
-- Name: identify(integer); Type: FUNCTION; Schema: public; Owner: telesto
--

CREATE FUNCTION identify(p_client_id integer) RETURNS TABLE(client_id integer, client_name character varying, operation_mode smallint)
    LANGUAGE sql
    AS $$   
    SELECT c.client_id, c.client_name, c.operation_mode FROM clients c WHERE c.client_id = p_client_id;
$$;


ALTER FUNCTION public.identify(p_client_id integer) OWNER TO telesto;

--
-- TOC entry 189 (class 1255 OID 16461)
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
		p_client_name, 
		p_operation_mode
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
    time_of_arrival timestamp without time zone,
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
-- TOC entry 1976 (class 0 OID 0)
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
-- TOC entry 1978 (class 0 OID 0)
-- Dependencies: 170
-- Name: queue_queue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: telesto
--

ALTER SEQUENCE queue_queue_id_seq OWNED BY queues.queue_id;


--
-- TOC entry 174 (class 1259 OID 16415)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: telesto
--

CREATE SEQUENCE users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_user_id_seq OWNER TO telesto;

--
-- TOC entry 1979 (class 0 OID 0)
-- Dependencies: 174
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: telesto
--

ALTER SEQUENCE users_user_id_seq OWNED BY clients.client_id;


--
-- TOC entry 1842 (class 2604 OID 16420)
-- Name: client_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY clients ALTER COLUMN client_id SET DEFAULT nextval('users_user_id_seq'::regclass);


--
-- TOC entry 1841 (class 2604 OID 16409)
-- Name: message_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY messages ALTER COLUMN message_id SET DEFAULT nextval('messages_message_id_seq'::regclass);


--
-- TOC entry 1840 (class 2604 OID 16401)
-- Name: queue_id; Type: DEFAULT; Schema: public; Owner: telesto
--

ALTER TABLE ONLY queues ALTER COLUMN queue_id SET DEFAULT nextval('queue_queue_id_seq'::regclass);


--
-- TOC entry 1965 (class 0 OID 16417)
-- Dependencies: 175
-- Data for Name: clients; Type: TABLE DATA; Schema: public; Owner: telesto
--

COPY clients (client_id, client_name, operation_mode) FROM stdin;
1	abc	1
2	123	5
3	123	5
4	123	5
5	123	5
15	bla	3
17	bla	3
18	bla	3
19	bla	3
20	bla	3
24	bla	1
25	bla	1
28	abc	1
29	blubberi	1
30	dola	1
31	dola	1
32	dola	1
33	dola	1
34	dola	1
35	dola	1
36	dola	1
37	dola	1
38	dola	1
39	dola	1
40	dola	1
41	dola	1
42	dola	1
43	dola	1
44	dola	1
45	dola	1
46	dola	1
47	dola	1
48	dola	1
49	dola	1
50	dola	1
51	dola	1
52	dola	1
53	dola	1
54	dola	1
\.


--
-- TOC entry 1963 (class 0 OID 16406)
-- Dependencies: 173
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: telesto
--

COPY messages (message_id, queue_id, sender_id, receiver_id, context, priority, time_of_arrival, message) FROM stdin;
\.


--
-- TOC entry 1980 (class 0 OID 0)
-- Dependencies: 172
-- Name: messages_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: telesto
--

SELECT pg_catalog.setval('messages_message_id_seq', 1, false);


--
-- TOC entry 1981 (class 0 OID 0)
-- Dependencies: 170
-- Name: queue_queue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: telesto
--

SELECT pg_catalog.setval('queue_queue_id_seq', 2, true);


--
-- TOC entry 1961 (class 0 OID 16398)
-- Dependencies: 171
-- Data for Name: queues; Type: TABLE DATA; Schema: public; Owner: telesto
--

COPY queues (queue_id, queue_name) FROM stdin;
1	first Queue
2	first Queue
\.


--
-- TOC entry 1982 (class 0 OID 0)
-- Dependencies: 174
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: telesto
--

SELECT pg_catalog.setval('users_user_id_seq', 54, true);


--
-- TOC entry 1850 (class 2606 OID 16414)
-- Name: pk_message_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT pk_message_id PRIMARY KEY (message_id);


--
-- TOC entry 1844 (class 2606 OID 16424)
-- Name: pk_queue_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY queues
    ADD CONSTRAINT pk_queue_id PRIMARY KEY (queue_id);


--
-- TOC entry 1852 (class 2606 OID 16422)
-- Name: pk_user_id; Type: CONSTRAINT; Schema: public; Owner: telesto; Tablespace: 
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT pk_user_id PRIMARY KEY (client_id);


--
-- TOC entry 1845 (class 1259 OID 16428)
-- Name: idx_receiver_queue_priority; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_priority ON messages USING btree (receiver_id, queue_id, priority DESC);


--
-- TOC entry 1846 (class 1259 OID 16427)
-- Name: idx_receiver_queue_priority_sender; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_priority_sender ON messages USING btree (receiver_id, queue_id, priority DESC, sender_id);


--
-- TOC entry 1847 (class 1259 OID 16431)
-- Name: idx_receiver_queue_time; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_time ON messages USING btree (receiver_id, queue_id, time_of_arrival);


--
-- TOC entry 1848 (class 1259 OID 16432)
-- Name: idx_receiver_queue_time_sender; Type: INDEX; Schema: public; Owner: telesto; Tablespace: 
--

CREATE INDEX idx_receiver_queue_time_sender ON messages USING btree (receiver_id, queue_id, time_of_arrival, sender_id);


--
-- TOC entry 1972 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: telesto
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM telesto;
GRANT ALL ON SCHEMA public TO telesto;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 1974 (class 0 OID 0)
-- Dependencies: 175
-- Name: clients; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE clients FROM PUBLIC;
REVOKE ALL ON TABLE clients FROM telesto;
GRANT ALL ON TABLE clients TO telesto;
GRANT ALL ON TABLE clients TO PUBLIC;


--
-- TOC entry 1975 (class 0 OID 0)
-- Dependencies: 173
-- Name: messages; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE messages FROM PUBLIC;
REVOKE ALL ON TABLE messages FROM telesto;
GRANT ALL ON TABLE messages TO telesto;
GRANT ALL ON TABLE messages TO PUBLIC;


--
-- TOC entry 1977 (class 0 OID 0)
-- Dependencies: 171
-- Name: queues; Type: ACL; Schema: public; Owner: telesto
--

REVOKE ALL ON TABLE queues FROM PUBLIC;
REVOKE ALL ON TABLE queues FROM telesto;
GRANT ALL ON TABLE queues TO telesto;
GRANT ALL ON TABLE queues TO PUBLIC;


--
-- TOC entry 1508 (class 826 OID 16395)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM PUBLIC;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres REVOKE ALL ON TABLES  FROM postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO postgres;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES  TO PUBLIC;


-- Completed on 2013-10-25 01:49:32

--
-- PostgreSQL database dump complete
--

