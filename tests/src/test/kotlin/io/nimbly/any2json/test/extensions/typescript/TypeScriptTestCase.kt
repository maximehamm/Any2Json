package io.nimbly.any2json.test.extensions.typescript

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
        assertEquals(toJson(), """
            {
              "name": "",
              "domains": [
                {}
              ],
              "company": {
                "logo": {},
                "name": "",
                "id": "",
                "subscription": {
                  "saveViews": false,
                  "versioning": false,
                  "serviceFocusGraph": false,
                  "messageFocusGraph": false,
                  "id": "",
                  "hierarchicalGraph": false,
                  "transportedDataGraph": false,
                  "enterprise": false,
                  "nbrFieldsLimit": 0,
                  "nbrServicesLimit": 0,
                  "nbrTypesLimit": 0,
                  "routeSequenceGraph": false,
                  "offer": {
                    "saveViews": false,
                    "versioning": false,
                    "serviceFocusGraph": false,
                    "messageFocusGraph": false,
                    "hierarchicalGraph": false,
                    "transportedDataGraph": false,
                    "nbrTypesLimit": 0,
                    "nbrFieldsLimit": 0,
                    "enterprise": false,
                    "stripeYearlyPriceId": "",
                    "nbrServicesLimit": 0,
                    "routeSequenceGraph": false,
                    "stripeMonthlyPriceId": "",
                    "nbrRoutesAndMessagesLimit": 0,
                    "domains": false,
                    "nbrFacadesLimit": 0,
                    "tags": false,
                    "type": "ENTERPRISE",
                    "serviceSequenceGraph": false,
                    "informations": false
                  },
                  "nbrRoutesAndMessagesLimit": 0,
                  "domains": false,
                  "nbrFacadesLimit": 0,
                  "tags": false,
                  "serviceSequenceGraph": false,
                  "informations": false
                },
                "url": "",
                "description": "",
                "age": 0
              },
              "userProject": {
                "description": {},
                "parent": "",
                "type": "",
                "name": "",
                "commit": ""
              },
              "teamProject": {
                "description": "",
                "parent": "",
                "type": "",
                "name": "",
                "commit": ""
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