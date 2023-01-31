package com.demo.newwifi.conf

object LocalConf {
    const val email="rubengjkerjw@gmail.com"
    const val url="https://sites.google.com/view/netguardwifianalyzer/%E9%A6%96%E9%A1%B5"

    const val OPEN="netG_open"
    const val HOME="netG_n_home"
    const val CONNECT_WIFI_DIALOG="netG_n_wifi"
    const val TEST_RESULT="netG_n_result"
    const val TEST_INTER="netG_i_t"
    const val CONNECT_WIFI="netG_i_b"


    const val localAd="""{
    "netG_click":15,
    "netG_show":50,
    "netG_open": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/3419835294",
            "netG_type": "open",
            "netG_priority": 1
        }
    ],
    "netG_n_home": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/2247696110",
            "netG_type": "native",
            "netG_priority": 2
        }
    ],
      "netG_n_wifi": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/2247696110",
            "netG_type": "native",
            "netG_priority": 2
        }
    ],
       "netG_n_result": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/2247696110",
            "netG_type": "native",
            "netG_priority": 2
        }
    ],
     "netG_i_t": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/8691691433",
            "netG_type": "inter",
            "netG_priority": 2
        }
    ],
     "netG_i_b": [
        {
            "netG_source": "admob",
            "netG_id": "ca-app-pub-3940256099942544/8691691433",
            "netG_type": "inter",
            "netG_priority": 2
        }
    ]
}"""
}