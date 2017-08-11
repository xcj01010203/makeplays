//初始化全局变量
function HashMap(){
	var size = 0;
	var entry =  new Object();
	
	this.put = function (key,value){
      if(!this.containsKey(key))
        {
             size ++ ;
       }
        entry[key] = value;
    }
	
	this.get = function (key){
         return this.containsKey(key) ? entry[key] : null;
     }
	//通过key获取value 要求value的值在map中不能重复
	this.getKey = function (value){
         return this.getKeyByValue(value);
     }
	
	this.getKeyByValue = function ( value ){
        for(var prop in entry)
          {
             if(entry[prop] == value)
             {
                return prop;
              }
         }
         return '';
    }
	 /**Map删除remove方法**/
    this.remove = function(key) {
        if (this.containsKey(key) && (delete entry[key])) {
            size--;
        }
    }
    /**所有的Value**/
    this.values = function() {
        var values = new Array();
        for (var prop in entry) {
            values.push(entry[prop]);
        }
        return values;
    }
    /**所有的 Key**/
    this.keys = function() {
        var keys = new Array();
        for (var prop in entry) {
            keys.push(prop);
        }
        return keys;
    }
    /**Map size**/
    this.size = function() {
        return size;
    }
    /**清空Map**/
    this.clear = function() {
        size = 0;
        entry = new Object();
    }
	this.toString = function (){
		var str ='';
        for(var prop in entry)
          {
             if(entry[prop]){
            	 if(str && str != ''){
            		 str +=','+entry[prop];
            	 }else{
            		 str =entry[prop];
            	 }
             }
         }
         return str;
    }
	 
	 this.containsValue = function ( value ){
        for(var prop in entry)
          {
             if(entry[prop] == value)
             {
                return true;
              }
         }
         return false;
    }
	
	this.containsKey = function ( key ){
        return (key in entry);
    }
}

//性别
var sexMap = new HashMap();
sexMap.put("0","女");
sexMap.put("1","男");

//功能类型
var operTypeMap = new HashMap();
operTypeMap.put("0","读");
operTypeMap.put("1","插入");
operTypeMap.put("2","修改");
operTypeMap.put("3","删除");
operTypeMap.put("4","批量导入");
operTypeMap.put("99","其他");

//菜单状态
var authorityStatusMap = new HashMap();
authorityStatusMap.put("0","有效");
authorityStatusMap.put("1","无效");

//季节
var seasonMap = new HashMap();
seasonMap.put("1","春");
seasonMap.put("2","夏");
seasonMap.put("3","秋");
seasonMap.put("4","冬");
seasonMap.put("99","未知");
//内外景
var siteMap = new HashMap();
siteMap.put("内","内景");
siteMap.put("外","外景");
siteMap.put("内外","内外景");
//场景类型
var typeMap = new HashMap();
typeMap.put("1","武戏");
typeMap.put("2","特效");
typeMap.put("3","武特");

//拍摄状态
var shootStatusMap = new HashMap();
shootStatusMap.put("0","未完成");
shootStatusMap.put("1","部分完成");
shootStatusMap.put("2","完成");
shootStatusMap.put("3","删戏");

//通告单拍摄状态
var noticeShootStatusMap = new HashMap();
noticeShootStatusMap.put("0","甩戏");
noticeShootStatusMap.put("1","部分完成");
noticeShootStatusMap.put("2","完成");
noticeShootStatusMap.put("3","删戏");
noticeShootStatusMap.put("4","加戏部分完成");
noticeShootStatusMap.put("5","加戏已完成");

//剧组类型0：电影；1：电视剧；2：网络剧；99：其他
var crewTypeMap = new HashMap();
crewTypeMap.put("0","电影");
crewTypeMap.put("1","电视剧");
crewTypeMap.put("2","网络剧");
crewTypeMap.put("3","网大");
crewTypeMap.put("99","其他");

//1：筹备中；0：拍摄中；2：已完成；99：冻结
var crewStatusMap = new HashMap();
crewStatusMap.put("1","筹备中");
crewStatusMap.put("0","拍摄中");
crewStatusMap.put("2","已完成");
crewStatusMap.put("99","冻结");

//行状态颜色
var viewStatusColor = new HashMap();
viewStatusColor.put("0","#E2FFE6");
viewStatusColor.put("1","#FEE9FA");
viewStatusColor.put("2","#FFBABA");
viewStatusColor.put("3","#D3F0FF");
viewStatusColor.put("4","#FEE9FA");
viewStatusColor.put("5","#FFBABA");

//角色类型
var viewRoleTypeMap = new HashMap();
viewRoleTypeMap.put("4", "待定");
viewRoleTypeMap.put("1", "主要演员");
viewRoleTypeMap.put("2", "特约演员");
viewRoleTypeMap.put("3", "群众演员");

var insideAdvertTypeMap = new HashMap();
insideAdvertTypeMap.put("1", "道具");
insideAdvertTypeMap.put("2", "台词");
insideAdvertTypeMap.put("99", "其他");


//通告单反馈状态
var fedBackStatusMap = new HashMap();
fedBackStatusMap.put("1", "未收取");
fedBackStatusMap.put("2", "已收到");
fedBackStatusMap.put("3", "已查看");

var febBackIsSatisfied = new HashMap();
febBackIsSatisfied.put("0", "有异议");
febBackIsSatisfied.put("1", "无异议");
febBackIsSatisfied.put("99", "");

//通告单反馈状态对应的字体颜色
var fedBackStatusColorMap = new HashMap();
fedBackStatusColorMap.put("1", "#e56d10");
fedBackStatusColorMap.put("2", "#0f96d9");
fedBackStatusColorMap.put("3", "black");
