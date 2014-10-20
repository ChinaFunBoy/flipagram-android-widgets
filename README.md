= flipagram-android-widgets

Various code and widgets used in the production of the Flipagram app for Android.

== TextWatchers

We use various TextWatchers to stylize and perform callbacks as the user types.

=== PatternCallbackTextWatcher

PatternCallbackTextWatcher watches the text typed by a user into an EditText and calls a Callback function if part of the input matches a Pattern.

=== PatternCharacterStyleTextWatcher

PatternCharacterStyleTextWatcher watches the text type by a user and applies a CharacterStyle to regions matching a Pattern.
