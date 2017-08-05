package org.icememo.model

class RepeatStatModel {
    int good
    int easy
    int hard
    int reset
    int suspended

    int getAll() {
        return good + easy + hard + reset + suspended;
    }
}
