# Any to Json

Developped by Maxime HAMM
_maxime.hamm.pro@gmail.com_

This plugin is compatible with all Jetbrain products

- `plugin` : main plugin code
- `extensions` : extensions per language :
    - `java` 
    - `kotlin`
    - `typescript`
    - ...
- `common` : code used by plugin and extensions

## Market place location
https://plugins.jetbrains.com/plugin/16055-any-to-json/

## Installation
Import using gradle (Gradle should use Java 11)
- Launch task `build` from root module
- Run all tests from module `./test`

## Run
Launch task `runIde` from `plugin` module

## Get ready to publish to market place
Setup a system variable :
- Edit .bash_profile` and add `export ORG_GRADLE_PROJECT_intellijPublishToken='xxxx'`
- Or launch this command : launchctl setenv ORG_GRADLE_PROJECT_intellijPublishToken 'xxxx'

_Token is known by Maxime HAMM :)_

## Prepare to publish a release
- Build using task `build` from root module 
- Run tests in `./test` module
- Verify compatibility using task `runPluginVerifier` from `plugin` module

## Publishing a new release
- Upgrade version in `./build.gradle`
- Update change note in `./plugin/build.gradle`
- Publish to marketplace using task `publishPlugin` from module `.plugin`

## Contributing to _Any to Json_

In order to let the plugin be compatible with any language,
do not add any dependency to Intellij IDEA plugin
in modules `plugin` and `common`. i.e do not change
this in `build.gradle` files :
```
intellij {
    version '2020.3'
    intellij.type = 'IC' 
}
 ```

To support new languages, you'll have to build a new module for it
and and use the extension point.
For example look how Java is supported at `plugin.xml` :
```
<depends optional="true" config-file="plugin-withJava.xml">
        com.intellij.java</depends>
```
Then look at `io.nimbly.any2json.Java2Json` implementation !

Enjoy !


