# -*- coding: utf-8 -*-

from sys import version_info
from setuptools import setup, find_packages

basename = "telesto"
version = "0.1"
pyversion = "%s.%s" % (version_info.major, version_info.minor)

setup(
    name=basename,
    version=version,
    packages=find_packages(),
    zip_safe=True,
    author="Simon Marti, Dominic Langengger",
    author_email="simarti@ethz.ch, dominicl@ethz.ch",
    description="Protocol code generator for Telesto Messaging Passing System",
    keywords="telesto protocol code generator message passing system",
    install_requires=("jinja2",)
)
