Distributed File Manager
ဤ Project သည် RMI (Remote Method Invocation) ကို အသုံးပြု၍ ဖိုင်များကို Server အများအပြားပေါ်တွင် ဖြန့်ဝေသိမ်းဆည်းခြင်းနှင့် စီမံခန့်ခွဲခြင်းတို့ကို လုပ်ဆောင်သော Distributed File System  တစ်ခု ဖြစ်ပါသည်။

လိုအပ်သောစနစ်များ (Prerequisites)
Java Development Kit (JDK) 8 သို့မဟုတ် အထက်

Eclipse IDE: Project ကို run ရန်အတွက် Eclipse IDE ကိုအသုံးပြုပါမည်။

စတင်အသုံးပြုပုံ (Getting Started)
ဤ Project သည် အဓိကအားဖြင့် အစိတ်အပိုင်း (၃) ခုဖြင့် ဖွဲ့စည်းထားပြီး၊ ၎င်းတို့ကို သီးခြားစီ run ရန်လိုအပ်ပါသည်။

၁။ Metadata Server ကို run ရန် (ဗဟိုဆာဗာ)
၂။ Storage Servers များကို run ရန် (ဖိုင် data သိမ်းဆည်းသောဆာဗာများ)
၃။ Client ကို run ရန် (အသုံးပြုသူ interface)

Step 1: Eclipse တွင် Project ကို Import ပြုလုပ်ခြင်း
Eclipse IDE ကိုဖွင့်ပါ။

File -> Import ကိုရွေးပါ။

General -> Existing Projects into Workspace ကိုရွေးပြီး Next ကိုနှိပ်ပါ။

Select root directory မှာ သင့် project folder (ဥပမာ- DistributedFileManager) ကို ရွေးပေးပါ။

Finish ကိုနှိပ်ပါ။ Project သည် Eclipse ထဲသို့ ရောက်ရှိသွားပါလိမ့်မည်။

Step 2: RMI Registry ကို စတင်ခြင်း
ဤ Project တွင် MetadataServerMain.java သည် RMI Registry ကို သူ့ဘာသာသူ စတင်ပေးနိုင်သောကြောင့် rmiregistry command ကို သီးခြားစီ run ရန်မလိုအပ်တော့ပါ။

Step 3: Servers များကို စတင်ခြင်း
Servers များကို run ရန်အတွက် Eclipse တွင် Run Configurations ကို အသုံးပြုပါမည်။

A) Metadata Server ကို စတင်ခြင်း
Metadata Server သည် RMI Registry ကိုပါ တစ်ပါတည်း စတင်ပေးမည်ဖြစ်ပါသည်။

၎င်းကို စက်တစ်လုံးတည်းတွင်သာ run ရန် လိုအပ်ပါသည်။

1.Eclipse တွင် MetadataServerMain.java file ကို Right-click နှိပ်ပါ။

2.Run As -> Java Application ကိုရွေးပါ။

3.Console မှာ Metadata Server Ready! ကို မြင်ရပါက အောင်မြင်ပါသည်။

B) Storage Servers များကို စတင်ခြင်း
Storage Server များသည် ဖိုင်များ၏ data အပိုင်းအစများကို လက်ခံသိမ်းဆည်းမည့်နေရာများ ဖြစ်ပါသည်။

လိုအပ်သလို Storage Server အများအပြားကို run နိုင်ပါသည်။

1.Eclipse တွင် StorageServerMain.java file ကို Right-click နှိပ်ပါ။

2.Run As -> Run Configurations... ကိုရွေးပါ။

3.Arguments tab ကိုရွေးပါ။

4.Program arguments box ထဲတွင် အောက်ပါအတိုင်း ထည့်ပေးပါ။

# Storage Server 1 အတွက်
storageServer1 chunks_server1

# Storage Server 2 အတွက်
# (အခြား run configuration အသစ်တစ်ခု ပြန်ဖန်တီးပါ)
storageServer2 chunks_server2
Apply ကိုနှိပ်ပြီး Run ကိုနှိပ်ပါ။ Console တွင် Output ပေါ်လာသည်ကို မြင်ရပါလိမ့်မည်။

Step 4: Client ကို စတင်ခြင်း
Client သည် အသုံးပြုသူမှ ဖိုင်များကို upload, download နှင့် list ကြည့်နိုင်မည့် application ဖြစ်ပါသည်။

1.Eclipse တွင် ClientUI.java file ကို Right-click နှိပ်ပါ။

2.Run As -> Java Application ကိုရွေးပါ။

3.ClientUI application ပွင့်လာပါလိမ့်မည်။

အနှစ်ချုပ်
ဖိုင်တစ်ခုကို Client က Upload ပြုလုပ်ပါက  ဖိုင်ကို အပိုင်းလေးများအဖြစ် ခွဲကာ Storage Server များပေါ်တွင် ဖြန့်ဝေသိမ်းဆည်းပါသည်။ Client သည်  Download ပြုလုပ်ပါကလည်း Chunks များကို Server အများအပြားမှ တစ်ပြိုင်နက်တည်း ပြန်လည်ယူဆောင်ကာ မူလဖိုင်ကို ပြန်လည်ပေါင်းစပ်ပါသည်။

