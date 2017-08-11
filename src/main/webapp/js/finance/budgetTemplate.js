//按制作周期

function productionCycle(){
	var ctx=$("input[name=contextPath]").val();
	var source = {
			dataType:'json',
			dataFields: [
			    {name: 'templateId',type:'string'},
			    {name: 'templateName',type:'string'},
			    {name: 'parentId',type:'string'}
			],
			hierarchy:{
				keyDataField: {name:'templateId'},
				parentDataField: {name:'parentId'}
			},
			id:'templateId',
			type:'post',
			url:'selectBudget?type=0'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#production").jqxTreeGrid({
		theme:theme,
		 width: 220,
		 maxHeight:clientHeight,
         source: dataAdapter,
         //sortable: true,
         selectionMode: "singleRow",
         ready: function()
         {
        	 $('#production').on('rowSelect', 
        			 function (event)
        			 {
        			     // event args.
        			     var args = event.args;
        			     var key = args.key;
        			     var row = $("#production").jqxTreeGrid('getRow', key);
        			     //console.log(row)
        			     if(row.expanded == true){
        			    	 $("#production").jqxTreeGrid('collapseRow', key);
        			     }else{
        			    	 $("#production").jqxTreeGrid('expandRow', key);
        			     }
        			     $("#production").jqxTreeGrid('clearSelection');
        			 });
         },
         columns: [
           { text: '财务科目', dataField: 'templateName', width: 200, align: "center" }
          
         ]
	});
}

//按部门
function bySector(){
	var ctx=$("input[name=contextPath]").val();
	var source = {
			dataType:'json',
			dataFields: [
			    {name: 'templateId',type:'string'},
			    {name: 'templateName',type:'string'},
			    {name: 'parentId',type:'string'}
			],
			hierarchy:{
				keyDataField: {name:'templateId'},
				parentDataField: {name:'parentId'}
			},
			id:'templateId',
			type:'post',
			url:'selectBudget?type=1'
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#sector").jqxTreeGrid({
		theme:theme,
		 width: 220,
		 maxHeight:clientHeight,
         source: dataAdapter,
         //sortable: true,
         ready: function()
         {
        	 $('#sector').on('rowSelect', 
        			 function (event)
        			 {
        			     // event args.
        			     var args = event.args;
        			     var key = args.key;
        			     var row = $("#sector").jqxTreeGrid('getRow', key);
        			     //console.log(row)
        			     if(row.expanded == true){
        			    	 $("#sector").jqxTreeGrid('collapseRow', key);
        			     }else{
        			    	 $("#sector").jqxTreeGrid('expandRow', key);
        			     }
        			     $("#sector").jqxTreeGrid('clearSelection');
        			 });
         },
         columns: [
           { text: '财务科目', dataField: 'templateName', width: 200, align: "center" }
          
         ]
	});
}



