--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: lsa_space; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE lsa_space (
    id integer NOT NULL,
    name text NOT NULL,
    db_url text NOT NULL,
    type text NOT NULL,
    status text NOT NULL,
    nsigma integer NOT NULL
);


ALTER TABLE public.lsa_space OWNER TO postgres;

--
-- Name: lsa_space_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE lsa_space_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lsa_space_id_seq OWNER TO postgres;

--
-- Name: lsa_space_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE lsa_space_id_seq OWNED BY lsa_space.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE lsa_space ALTER COLUMN id SET DEFAULT nextval('lsa_space_id_seq'::regclass);


--
-- Name: lsa_space_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY lsa_space
    ADD CONSTRAINT lsa_space_name_key UNIQUE (name);


--
-- Name: lsa_space_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY lsa_space
    ADD CONSTRAINT lsa_space_pkey PRIMARY KEY (id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
