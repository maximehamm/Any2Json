/*
 * ANY2JSON
 * Copyright (C) 2021  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
 *
 * This document is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.nimbly.any2json.test.yaml

import io.nimbly.any2json.test.AbstractTestCase

class YamlTestCase : AbstractTestCase() {

    fun testYaml() {

        // language=Yaml
        configure("""
            ---
            - hosts: webservers
            
              vars:
                http_port: 80
                max_clients: 200
            
              remote_user: root
            
              tasks:
                - name: ensure apache is at the latest version
                  yum:
                    name: httpd
                    state: latest
            
                - name: write the apache config file
                  template:
                    src: /srv/httpd.j2
                    dest: /etc/httpd.conf
                  notify:
                    - restart apache
            
                - name: ensure apache is running
                  service:
                    name: httpd
                    state: started
            
              handlers:
                - name: restart apache
                  service:
                    name: httpd
                    state: restarted
            """)

        // language=Json
        assertEquals(copy(), """
            [
              {
                "hosts": "webservers",
                "vars": {
                  "http_port": 80,
                  "max_clients": 200
                },
                "remote_user": "root",
                "tasks": [
                  {
                    "name": "ensure apache is at the latest version",
                    "yum": {
                      "name": "httpd",
                      "state": "latest"
                    }
                  },
                  {
                    "name": "write the apache config file",
                    "template": {
                      "src": "/srv/httpd.j2",
                      "dest": "/etc/httpd.conf"
                    },
                    "notify": [
                      "restart apache"
                    ]
                  },
                  {
                    "name": "ensure apache is running",
                    "service": {
                      "name": "httpd",
                      "state": "started"
                    }
                  }
                ],
                "handlers": [
                  {
                    "name": "restart apache",
                    "service": {
                      "name": "httpd",
                      "state": "restarted"
                    }
                  }
                ]
              }
            ]
        """.trimIndent())
    }



    fun configure(text: String) {
        myFixture.configureByText("test.yaml", text.trimIndent())
    }
}