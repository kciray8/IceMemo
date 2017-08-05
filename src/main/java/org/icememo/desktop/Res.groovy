package org.icememo.desktop

import java.awt.Image

class Res {
    static Image logo16 = Utils.createImage("/logo16.png", "IceMemo logo (spiral)")
    static Image logo32 = Utils.createImage("/logo32.png", "IceMemo logo (spiral)")

    static List<Image> getLogoImages(){
        return [logo16, logo32]
    }
}
