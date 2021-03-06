package com.xiaotu.makeplays.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

/**
 * @类名 SocketUtil
 * @日期 2017年7月3日
 * @作者 高海军
 * @功能 socket帮助类
 */
public class SocketClientUtil
{
	private static final int BUFFER_SIZE = 256;
	
	public static final String EOF = "#eof#";
	
	private SocketChannel socketChannel;
	private Selector selector;
	
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		System.out.println(new SocketClientUtil("192.168.10.59", 8001).send("{\"datas\":[{\"majorView\":\"上海马路上\",\"shootRegion\":\"\",\"minorView\":\"大钟寺\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"6415574a52d5453c968422ee7e9fc730\"},{\"majorView\":\"场景1\",\"shootRegion\":\"\",\"minorView\":\"场景2\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"30056a342c6743e0bdd5443d1d41752f\"},{\"majorView\":\"上海马路上\",\"shootRegion\":\"上海市-松江区\",\"minorView\":\"大钟寺\",\"shootLocation\":\"上海警署\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"d6b47d4df4ac44ce96eff1dcffef96a4\"},{\"majorView\":\"上海外滩\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"6487f3ba2474422b9b7277a5117632ad\"},{\"majorView\":\"上海外滩\",\"shootRegion\":\"上海市-松江区\",\"minorView\":\"上海外滩\",\"shootLocation\":\"北京天安门\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"4471bfb7632043849771f2f30dbb4b80\"},{\"majorView\":\"《字林西报》大楼\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"66cb69ec96354f8cb4b4377a97f54aa3\"},{\"majorView\":\"上海外滩\",\"shootRegion\":\"\",\"minorView\":\"明珠塔\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"de61498840824345b32b7a37da6ac1f3\"},{\"majorView\":\"上海外滩\",\"shootRegion\":\"\",\"minorView\":\"上海外滩\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cac61b3f5cd94ac389b6e3cffad3b98c\"},{\"majorView\":\"上海外滩江岸\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cbe5f3168cd44030aa89cd69f3dc7621\"},{\"majorView\":\"上海外滩江岸\",\"shootRegion\":\"\",\"minorView\":\"大钟寺\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"ae2b383e72514407a5a5416d2ca4a131\"},{\"majorView\":\"医院急诊室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cabde27e709542849ebbc9d46c89d3d4\"},{\"majorView\":\"医院急诊病房\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"14f22e478f2343ec8cce2b7973de462a\"},{\"majorView\":\"病房外走廊\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"671f34368f9f4e4b8e9b5533ac63b4fd\"},{\"majorView\":\"急诊病房\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"07a5eb8030454e43aafdd7905dd2d00f\"},{\"majorView\":\"黄浦潮剧社\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"34cc427a26cf4120836d9f14e29cebe8\"},{\"majorView\":\"上海街道\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"上海市监狱\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"30b1120fe8c4460fb4c3212db580b758\"},{\"majorView\":\"提篮桥监狱门口\",\"shootRegion\":\"北京市-西城区\",\"minorView\":\"\",\"shootLocation\":\"天桥剧场\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"f1ed740fb28345e28d484227caf05d02\"},{\"majorView\":\"提篮桥监狱监房\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"151b7cbdc3a74dd7a9dcdf6949df8f00\"},{\"majorView\":\"提篮桥监狱门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"bd887a182b3e442da016de6d7cc26539\"},{\"majorView\":\"一幢中式大房子\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"f2d69176bb3049008d04f471a0676075\"},{\"majorView\":\"上海马路上\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"dd07ef6d20194a00aa94692285736ca8\"},{\"majorView\":\"穆先生的家\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"ec5f80ec2315451e9c9d4a2d65b9edab\"},{\"majorView\":\"外滩华懋饭店前\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"adc877c02c0340b3b728b5c55dac3a2d\"},{\"majorView\":\"\",\"shootRegion\":\"上海市-松江区\",\"minorView\":\"\",\"shootLocation\":\"北京天安门\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"95c82f6f7d5a4906882669c375c37db1\"},{\"majorView\":\"华懋饭店一楼酒吧\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"22f52256bb9848ac9b4107954ef333a3\"},{\"majorView\":\"沪东女子中学\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"√\",\"viewId\":\"a955343d61194f0d8e245d540e771ebf\"},{\"majorView\":\"沪东女子中学校门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"20aa9155b5eb4725a92d8cfa01b2926a\"},{\"majorView\":\"沪东女子中学教堂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"√\",\"viewId\":\"f3d051e1a92741ab856e7a686da98978\"},{\"majorView\":\"校园教堂附近\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"√\",\"viewId\":\"7674aab3a0044f468d038595b74d2ea6\"},{\"majorView\":\"黄浦潮剧社\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"c36ab94a28ad49e1b402242ce9988fc4\"},{\"majorView\":\"华懋饭店酒吧\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b0ce348fb8204f9ebcf5b19454932d99\"},{\"majorView\":\"吧台\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"a54f266723894df79ef3a7931c12d293\"},{\"majorView\":\"南京路国际饭店\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"1230278a0dce455899ca1594738fdfac\"},{\"majorView\":\"酒吧一角\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"e155dc76f3104476af422db8f4053f7d\"},{\"majorView\":\"吧台\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"631200c9ae58413088d33e62d92d397d\"},{\"majorView\":\"酒吧间\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"942628308fdd4d69a208a9ccf8494c94\"},{\"majorView\":\"国际饭店\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"352518d5ef6f4fecab381717774a8945\"},{\"majorView\":\"上海马路上\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"c0feae9f99e3460f9bbfdefec7319267\"},{\"majorView\":\"国际饭店餐厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"ed9524291fd3455394343bce79e9a783\"},{\"majorView\":\"苏州河外白渡桥\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"a2471096a6684250aae72280eefd88e6\"},{\"majorView\":\"沪东女子中学校门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"42f8bd322d934eeaac04cfd8480337b6\"},{\"majorView\":\"法租界李宅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b421e8aabeba4205ac5643ebbeca2ea1\"},{\"majorView\":\"李宅餐厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"3c9b2cfc82374ff0be6281f76bda03de\"},{\"majorView\":\"李宅门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"9f5e5ced8cea43d69620625435d65254\"},{\"majorView\":\"李宅餐厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"9ad9b0fdd4ad479f87864e87e0d58b58\"},{\"majorView\":\"李宅门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"eec6ae4dcc944caba0f3243ce676d771\"},{\"majorView\":\"南京某宾馆门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"35fe473c6f0b4ec99e537b7c1a2ad6ef\"},{\"majorView\":\"日本驻南京总领事馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b10d34d4686142dba38420b3a8849bb8\"},{\"majorView\":\"领事馆院子\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"c75cdd933b574da586ac8d2f68cf9917\"},{\"majorView\":\"宴会厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"e877f74d1d394c92807c76be58fc0528\"},{\"majorView\":\"南京闹事区\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"74b6a31ad58c4e4b959fcd66945aed0c\"},{\"majorView\":\"一幢普通的四层楼房\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8356e3b311004d3eae5c6ae05cf61de3\"},{\"majorView\":\"南京商业街\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"d390c656a2844e789626cf06c3000170\"},{\"majorView\":\"楼上房间\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"19318bdc107b41fa80af05812390b24b\"},{\"majorView\":\"南京朱伟勋办公室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"960fb3d6a2604a539ce2983c5d8565ec\"},{\"majorView\":\"正金银行南京分行营业厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"863305a4c6704a89942fd4fc67d8b3be\"},{\"majorView\":\"朱伟勋小公馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8c6f0b28dd4e485ebe609da1fc982c84\"},{\"majorView\":\"正金银行南京分行\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"d8fb7048619c4b7fb7336a93fe82aca1\"},{\"majorView\":\"朱伟勋小公馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"e34e7dc850f34391baef03141b4f90d4\"},{\"majorView\":\"朱伟勋小公馆客厅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8c7f214b385d480394c8a95ad24786d4\"},{\"majorView\":\"正金银行南京分行\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"a74ae999e71c4cc9bf647a88e6d41f33\"},{\"majorView\":\"上海县郊中国军队驻地\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"fef517580aca41d2af9f2026ba203edd\"},{\"majorView\":\"黄浦江码头\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"c03bc2606e364af8aca53ecf2863ad62\"},{\"majorView\":\"南京路73号上海美术照相馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"0d2faf87f33a4c72906cf9c8476690e9\"},{\"majorView\":\"照相馆第八工作室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"eb376991c7cb4aaa8b0efd9a4e47b6dd\"},{\"majorView\":\"美术照相馆门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"16654ea369d142498668f603a0694b33\"},{\"majorView\":\"照相馆第八工作室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cd872f86ec204502a8aa3423cd2838dd\"},{\"majorView\":\"照相馆第一工作室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"62cb7072fe694b749a23bfb0b4f9344d\"},{\"majorView\":\"照相馆店堂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"0cfa675bd464445ba090c352d67bfefc\"},{\"majorView\":\"照相馆店堂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"df18bf5d624f4fd0ac69c87c1cbf4378\"},{\"majorView\":\"华懋饭店酒吧\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"a33c011925bc484ca560a8906a691760\"},{\"majorView\":\"酒吧柜台\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"3da54550a3154bb89c7d836a82bd6b05\"},{\"majorView\":\"酒吧店堂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"dfa97a47d10d41048ff885f9895990e8\"},{\"majorView\":\"《字林西报》大楼\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8a773a92e8594f8bac95064b5fbf21d4\"},{\"majorView\":\"上海郊外\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"56efec258676437fa30d76c55a3c3c90\"},{\"majorView\":\"虹口八字桥一带\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"7032b4ee18c14259b33390fd8207c264\"},{\"majorView\":\"大公纱厂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"2775b70d5408472ea958380e4e8e0b49\"},{\"majorView\":\"美可制冰厂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"49a2a822c89d4a52bc37c916c398f442\"},{\"majorView\":\"制冰厂公务楼\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8c40689c8da74cee8a51ae355d299d0b\"},{\"majorView\":\"美可制冰厂公务楼\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"a5d9abe9057b46e98a50f65a4d1f12c7\"},{\"majorView\":\"制冰厂公务搂楼下\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"c2a811ae92cb46b78b9a82e30752380d\"},{\"majorView\":\"上海郊外\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"96f5a6f1225a4068bc59732ea0ce41a5\"},{\"majorView\":\"大隆染织厂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"038397fb2b804b4ba00a56f7c152753b\"},{\"majorView\":\"大隆染织厂厂门口\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"7a894ba37aba49e2b3ab1485151dc743\"},{\"majorView\":\"经理办公室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"6e641c7f33c44e4f85b6048d218fb87e\"},{\"majorView\":\"车间\",\"shootRegion\":\"上海市-松江区\",\"minorView\":\"\",\"shootLocation\":\"北京天安门\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"ac610a19b8b24241a5d508f727105adc\"},{\"majorView\":\"李唯亭办公室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"674c5917b6044821b3cd98955e4046bb\"},{\"majorView\":\"大隆染织厂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cc75b2851d234c589de904d0b8f210de\"},{\"majorView\":\"大隆染织厂经理办公室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"df2a6dcf6e814bc0be4a5c3cb5f77d1e\"},{\"majorView\":\"佐藤指挥室\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b71b545d45f7408eb15cf3067ccfa8b3\"},{\"majorView\":\"大隆染织厂厂区\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"7a9a4283afb04e0886032b88251c60d1\"},{\"majorView\":\"大隆厂公务楼\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"cb04e6f205884290a2f7d985ccf0dede\"},{\"majorView\":\"大隆厂水泵房\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"82fab95f279d415498f121fad905711b\"},{\"majorView\":\"上海郊外\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"420f2784e23c4d00a7753d1a31f80789\"},{\"majorView\":\"日本海军陆战队总部\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b9b46c0f838c441d9abc7de506aaf092\"},{\"majorView\":\"八十八师师部\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"700ad9daf9824905b6faf9f32fdda108\"},{\"majorView\":\"陆战队总部\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"29cb8ec5ab2a40f9afca79a5e59507aa\"},{\"majorView\":\"八十八师师部\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"713d3c0ebdff429c9fb4cb2e15a2b880\"},{\"majorView\":\"日本驻沪领事馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"46479b745f134dd2b45580f6dfe4cdba\"},{\"majorView\":\"大隆厂\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"5039c36db6764ebcb82a49c224f7dec4\"},{\"majorView\":\"《字林西报》社\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"036689a96d864f31bba47d13814a85df\"},{\"majorView\":\"九星大戏院\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"b5651364b5074747b409c1d1c9955b67\"},{\"majorView\":\"九星大戏院\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"e0619acaf9c147e9bf5c232a9e53911d\"},{\"majorView\":\"九星大戏院\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"3a3fc84b77404ffaae30616b47dede9c\"},{\"majorView\":\"上海马路上\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"6af2529898324cf8ad04632346a7daec\"},{\"majorView\":\"华懋饭店酒吧\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"c1bb8d498591474c93f72830d7a23f87\"},{\"majorView\":\"南京\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"a5879a982d19482e82bb426f8f6b974f\"},{\"majorView\":\"某夜总会\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"dbd7657a4700413a80b286cd83f46e37\"},{\"majorView\":\"舞厅风扇旁\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"ebf3d0a6e85840b59f4bd64f8fc43877\"},{\"majorView\":\"南京至上海的公路上\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"8c0e114b44064bf88e892a83a17b71db\"},{\"majorView\":\"上海郊外\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"98b66d01abfa4052a13b3e8718151dbb\"},{\"majorView\":\"上海日本领事馆\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"e6d477b227e64225a4dbd657babef84b\"},{\"majorView\":\"上海郊外\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"2e045df08b824c56bc5fcce48f588d02\"},{\"majorView\":\"李唯亭住宅\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"\",\"李香梅\":\"\",\"viewId\":\"572c039db9ec428e889ce8e81ba183a3\"},{\"majorView\":\"懋饭店\",\"shootRegion\":\"\",\"minorView\":\"\",\"shootLocation\":\"\",\"龚宇飞\":\"飞\",\"李香梅\":\"\",\"viewId\":\"c332576eaac849c68a86ec7053883ecc\"}],\"condition\":[\"龚宇飞\",\"李香梅\"],\"viewRoleNames\":\"护士,李唯亭,门徒,龚总管,看守,谢晋元,汪师傅,龚宇飞,范吟月,李香梅,大岛茂,门卫大嫂,沙逊,外国女教师,胡彩华,冯圣法,徐佳林,门房,王爱琴,佐藤,孙元良,真由子,朝鲜革命党人,龚宇伟,医生,朱伟勋,骆瑶琴,骆清,李丹沪,马赫,张柏亭\"}"));
	}
	
	public SocketClientUtil(String host, int port)
	{
		try
		{
			this.initClient(host, port);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 请求服务端数据
	 * @param sendData 请求数据
	 * @return 相应数据
	 */
	public String send(String sendData)
	{
		try
		{
			this.writeData((sendData + this.EOF).getBytes());
			return this.readData();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				this.close();
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
	
	/**
	 * 读取服务端数据
	 * @return 数据内容
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readData() throws IOException, UnsupportedEncodingException
	{
		byte[] data = null;
		int select = selector.select();
		if (select > 0)
		{
			data = new byte[0];
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iter = keys.iterator();
			while (iter.hasNext())
			{
				SelectionKey sk = iter.next();
				if (sk.isReadable())
					data = ArrayUtils.addAll(data, this.readData(sk));
				iter.remove();
			}
		}
		return new String(data);
	}
	
	/**
	 * 读取服务端数据
	 * @param sk
	 * @return 数据内容
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private byte[] readData(SelectionKey sk)
			throws IOException, UnsupportedEncodingException
	{
		SocketChannel curSc = (SocketChannel) sk.channel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		byte[] data = new byte[0];
		while (curSc.read(buffer) > 0)
		{
			buffer.flip();
			byte[] temp = new byte[buffer.limit()];
			buffer.get(temp);
			data = ArrayUtils.addAll(data, temp);
			buffer.clear();
		}
		return data;
	}
	
	/**
	 * 写入请求数据
	 * @param data 请求数据
	 * @throws IOException
	 */
	private void writeData(byte[] data) throws IOException
	{
		try
		{
			ByteBuffer buffer = ByteBuffer.wrap(data);
			while (buffer.hasRemaining())
				socketChannel.write(buffer);
		}
		catch (IOException e)
		{
			if (socketChannel.isOpen())
				socketChannel.close();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 初始化客户端链接
	 * @param host 服务端主机
	 * @param port 端口
	 * @throws IOException
	 * @throws ClosedChannelException
	 */
	private void initClient(String host, int port)
			throws IOException, ClosedChannelException
	{
		InetSocketAddress addr = new InetSocketAddress(host, port);
		socketChannel = SocketChannel.open();
		
		selector = Selector.open();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		// 连接到server
		socketChannel.connect(addr);
		
		while (!socketChannel.finishConnect())
		{
		}
	}
	
	/**
	 * 停止客户端
	 * @throws IOException
	 */
	private void close() throws IOException
	{
		if (selector != null && selector.isOpen())
			selector.close();
		if (socketChannel != null && socketChannel.isOpen())
			socketChannel.close();
	}
}
