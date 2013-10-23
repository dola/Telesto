{% macro emit(field, suffix="") -%}
    {%- import "emit.java" as _self -%}

    {{- _self.types[field.__class__.__name__](field, suffix) -}}
{%- endmacro %}

{% macro common(field, suffix="") -%}
    buffer.put{{ field.java_type.capitalize() }}({{ field.java_name }}{{ suffix }});
{%- endmacro %}

{% macro byte(field, suffix="") -%}
    buffer.put({{ field.java_name }}{{ suffix }});
{%- endmacro %}

{% macro boolean(field, suffix="") -%}
    putBoolean(buffer, {{ field.java_name }}{{ suffix }});
{%- endmacro %}

{% macro string(field, suffix="") -%}
    putString(buffer, {{ field.java_name }}{{ suffix }});
{%- endmacro %}

{% macro message(field, suffix="") -%}
    putMessage(buffer, {{ field.java_name }}{{ suffix }});
{%- endmacro %}

{% macro list(field, suffix="") -%}
    {%- import "emit.java" as _self -%}

        buffer.putInt({{ field.java_name }}.length);
        for (int i = 0; i < {{ field.java_name}}.length; i++) {
            {{ _self.emit(field._field, "[i]") }}
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
    'List': list
} %}
