package io.nimbly.any2json.test.plugin.yaml

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
        assertEquals(toJson(), """
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