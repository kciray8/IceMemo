package org.icememo.lang



class Language {
    List<LangEl> list = []
    String code

    public String get(String key){
        for(LangEl el: list){
            if(el.key == key){
                return el.value
            }
        }
        return "---"
    }
}
