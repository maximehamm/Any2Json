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

package io.nimbly.any2json.test.typescript

import io.nimbly.any2json.test.AbstractTestCase

class TypeScriptTestCase : AbstractTestCase() {

    @Suppress("TypeScriptUnresolvedVariable")
    fun testTypeScript() {

        // language=Ts
        configure("""
            export interface Project extends BaseModel {
                name?: string;
                domains?: any[];
                company?: Company;
                userProject?: {
                    name: string,
                    parent: string,
                    type: string,
                    description: unknown,
                    commit: string
                };
                teamProject?: {
                    name: string,
                    parent: string,
                    type: string,
                    description: string,
                    commit: string
                };
            }
            
            export interface Company {
                id?: string;
                url?: string;
                name?: string;
                description?: string;
                logo?: object;
                subscription?: Subscription;
                age: number
            }
            
            export interface Subscription extends Partial<PaidFeatures> {
                id?: string;
                offer: Offer;
            }
            
            export interface Offer extends PaidFeatures {
                type: OfferType;
                stripeMonthlyPriceId: string;
                stripeYearlyPriceId: string;
            }
            
            export type OfferType = 'ENTERPRISE' | 'PREMIUM' | 'LIGHT' | 'START';
            
            export interface BooleanPaidFeatures {
                serviceFocusGraph: boolean;
                messageFocusGraph: boolean;
                serviceSequenceGraph: boolean;
                transportedDataGraph: boolean;
                informations: boolean;
                domains: boolean;
                saveViews: boolean;
                hierarchicalGraph: boolean;
                routeSequenceGraph: boolean;
                enterprise: boolean;
                versioning: boolean;
                tags: boolean;
            }
            
            export interface PaidFeatures extends BooleanPaidFeatures {
                nbrTypesLimit: number;
                nbrFieldsLimit: number;
                nbrFacadesLimit: number;
                nbrServicesLimit: number;
                nbrRoutesAndMessagesLimit: number;
            }
            
            export interface BaseModel {
                createdAt?: string;
                updatedAt?: string;
                id?: bigint;
            }
            """)

        // language=Json
        assertEquals(copy(), """
            {
              "name": "Something",
              "domains": [
                {}
              ],
              "company": {
                "logo": {},
                "name": "Something",
                "id": "Something",
                "subscription": {
                  "saveViews": true,
                  "versioning": true,
                  "serviceFocusGraph": true,
                  "messageFocusGraph": true,
                  "id": "Something",
                  "hierarchicalGraph": true,
                  "transportedDataGraph": true,
                  "enterprise": true,
                  "nbrFieldsLimit": 100,
                  "nbrServicesLimit": 100,
                  "nbrTypesLimit": 100,
                  "routeSequenceGraph": true,
                  "offer": {
                    "saveViews": true,
                    "versioning": true,
                    "serviceFocusGraph": true,
                    "messageFocusGraph": true,
                    "hierarchicalGraph": true,
                    "transportedDataGraph": true,
                    "nbrTypesLimit": 100,
                    "nbrFieldsLimit": 100,
                    "enterprise": true,
                    "stripeYearlyPriceId": "Something",
                    "nbrServicesLimit": 100,
                    "routeSequenceGraph": true,
                    "stripeMonthlyPriceId": "Something",
                    "nbrRoutesAndMessagesLimit": 100,
                    "domains": true,
                    "nbrFacadesLimit": 100,
                    "tags": true,
                    "type": "ENTERPRISE",
                    "serviceSequenceGraph": true,
                    "informations": true
                  },
                  "nbrRoutesAndMessagesLimit": 100,
                  "domains": true,
                  "nbrFacadesLimit": 100,
                  "tags": true,
                  "serviceSequenceGraph": true,
                  "informations": true
                },
                "url": "Something",
                "description": "Something",
                "age": 100
              },
              "userProject": {
                "description": {},
                "parent": "Something",
                "type": "Something",
                "name": "Something",
                "commit": "Something"
              },
              "teamProject": {
                "description": "Something",
                "parent": "Something",
                "type": "Something",
                "name": "Something",
                "commit": "Something"
              }
            }
        """.trimIndent())
    }


    fun configure(text: String) {
        var t = text.trimIndent()
        t = t.substringBefore("interface ") + "interface <caret>" + t.substringAfter("interface ")
        myFixture.configureByText("test.ts", t )
    }
}