# flipagram-android-widgets

Various code and widgets used in the production of the Flipagram app for Android.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Installation](#installation)
- [TextWatchers](#textwatchers)
  - [PatternCallbackTextWatcher](#patterncallbacktextwatcher)
  - [PatternCharacterStyleTextWatcher](#patterncharacterstyletextwatcher)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Installation

Add this project as a submodule to your project

```
git submodule add flipagram-android-widgets git@github.com:Cheers-Dev/flipagram-android-widgets.git
```

Add this module to your settings.gradle and build.gradle.

settings.gradle (example)
```
...
include ':widgets'

project(':widgets').projectDir = new File(settingsDir, 'flipagram-android-widgets/widgets')
```

build.gradle (example)
```
...
   compile project(':widgets')
...
```


## TextWatchers

We use various TextWatchers to stylize and perform callbacks as the user types.

### PatternCallbackTextWatcher

PatternCallbackTextWatcher watches the text typed by a user into an EditText and calls a Callback function if part of the input matches a Pattern.

### PatternCharacterStyleTextWatcher

PatternCharacterStyleTextWatcher watches the text type by a user and applies a CharacterStyle to regions matching a Pattern.

## License
Copyright (C) 2015 Flipagram, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
