{% macro parse(field) -%}
    {%- import "parse.java" as _self -%}

    {{ field.java_name }} = {{ _self.parse_raw(field) -}}
{%- endmacro %}

{% macro parse_raw(field) -%}
    {%- import "parse.java" as _self -%}

    {{- _self.types[field.__class__.__name__](field) -}}
{%- endmacro %}

{% macro common(field) -%}
    buffer.get{{ field.java_type.capitalize() }}();
{%- endmacro %}

{% macro byte(field) -%}
   buffer.get();
{%- endmacro %}

{% macro boolean(field) -%}
    getBoolean(buffer);
{%- endmacro %}

{% macro string(field) -%}
    getString(buffer);
{%- endmacro %}

{% macro message(field) -%}
    getMessage(buffer);
{%- endmacro %}

{% macro queue(field) -%}
    getQueue(buffer);
{%- endmacro %}

{% macro list(field) -%}
    {%- import "parse.java" as _self -%}

        new {{ field._field.java_type }}[buffer.getInt()];
        for (int i = 0; i < {{ field.java_name }}.length; i++) {
            {{ field.java_name }}[i] = {{ _self.parse_raw(field._field) }}
        }
{%- endmacro %}

{% set types = {
    'Bool': boolean,
    'Byte': byte,
    'Short': common,
    'Int': common,
    'Long': common,
    'Float': common,
    'Double': common,
    'String': string,
    'TelestoMessage': message,
    'TelestoQueue': queue,
    'List': list
} %}
