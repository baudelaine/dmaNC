
var datas = [];
var tables = [];
var modelList = [];
var $tableList = $('#tables');
var $datasTable = $('#DatasTable');
var $navTab = $('#navTab');
var $refTab = $("a[href='#Reference']");
var $finTab = $("a[href='#Final']");
var $qsTab = $("a[href='#QuerySubject']");
var $secTab = $("a[href='#Security']");
var $traTab = $("a[href='#Translation']");
var activeTab = "Final";
var previousTab;
var $activeSubDatasTable;
var $newRowModal = $('#newRowModal');
var $modelListModal = $('#modModelList');
var $projectFileModal = $('#modProjectFile');
// var url = "js/PROJECT.json";
var qs2rm = {qs: "", row: "", qsList: [], ids2rm: {}};
var newRelation;
var cognosLocales;
var dbDataType = [];

var relationCols = [];
// relationCols.push({field:"checkbox", checkbox: "true"});
relationCols.push({field:"index", title: "index", formatter: "indexFormatter", sortable: false});
// relationCols.push({field:"_id", title: "_id", sortable: true});
relationCols.push({field:"key_name", title: "key_name", sortable: true});
relationCols.push({field:"key_type", title: "key_type", sortable: true});
relationCols.push({field:"pktable_name", title: "pktable_name", sortable: true});
relationCols.push({field:"pktable_alias", title: "pktable_alias", class: "pktable_alias", editable: {type: "text", mode: "inline"}, sortable: true, events: "pktable_aliasEvents"});
relationCols.push({field:"label", title: "Label", sortable: true, editable: {type: "textarea", mode: "inline", rows: 4}});
relationCols.push({field:"description", title: "Description", sortable: false});
relationCols.push({field:"recCountPercent", title: "count(*) %", sortable: true});
relationCols.push({field:"relationship", title: "relationship", editable: {type: "textarea", mode: "inline", rows: 4}});
relationCols.push({field:"fin", title: "fin", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"ref", title: "ref", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"sec", title: "sec", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"tra", title: "tra", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"nommageRep", title: "RepTableName", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"above", title: "Above", editable: {type: "text", mode: "inline"}, align: "center"});
// relationCols.push({field:"above", title: "Above", formatter: "aboveFormatter", align: "center", events: "aboveEvents"});
// relationCols.push({field:"above", title: "Above", formatter: "aboveFormatter", align: "center", events: "aboveEvents"});
relationCols.push({field:"leftJoin", title: "Left Join", formatter: "boolFormatter", align: "center"});
// relationCols.push({field:"usedForDimensions", title: "Used For Dimensions", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"usedForDimensions", title: "Used For Dimensions", editable: {type: "text", mode: "inline"}, align: "center"});

var usedForDimensionsSelect = {
  type: "select",
  mode: "inline"
};


// relationCols.push({field:"rightJoin", title: "Right Join", formatter: "boolFormatter", align: "center"});
relationCols.push({field:"duplicate", title: '<i class="glyphicon glyphicon-duplicate"></i>', formatter: "duplicateFormatter", align: "center"});
relationCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeFormatter", align: "center"});
// relationCols.push({field:"operate", title: "operate", formatter: "operateRelationFormatter", align: "center", events: "operateRelationEvents"});

// relationCols.push({field:"linker", formatter: "boolFormatter", align: "center", title: "linker"});
// relationCols.push({field:"linker_ids", title: "linker_ids"});

window.pktable_aliasEvents = {
      'change .pktable_alias': function (e, value, row, index) {
        alert("value=" + value);
      }
}

// function pktable_aliasFormatter(value, row, index){
//   return '<a href="#" id="pktable_alias">' + value + 'superuser</a>'
// }
//
$('pktable_alias .editable').on('update', function(e, editable) {
    alert('new value: ' + editable.value);
});

var newRelationCols = [];

newRelationCols.push();

var qsCols = [];
// qsCols.push({field:"checkbox", checkbox: "true"});
qsCols.push({field:"index", title: "index", formatter: "indexFormatter", sortable: false});
// qsCols.push({field:"_id", title: "_id", sortable: true});
qsCols.push({field:"table_name", title: "table_name", sortable: true});
qsCols.push({field:"table_alias", title: "table_alias", editable: false, sortable: true});
qsCols.push({field:"type", title: "type", sortable: true});
// qsCols.push({field:"visible", title: "visible", formatter: "boolFormatter", align: "center", sortable: false});
qsCols.push({field:"folder", title: "Folder", editable: {type: "text", mode: "inline"}, sortable: true});
qsCols.push({field:"filter", title: "filter", editable: {type: "textarea", mode: "inline"}, sortable: true});
qsCols.push({field:"label", title: "label", editable: {type: "textarea", mode: "inline"}, sortable: true});
qsCols.push({field:"description", title: "Description", sortable: false, editable: {type: "textarea", mode: "inline", rows: 4}});
qsCols.push({field:"recCount", title: "count(*)", sortable: true});
qsCols.push({field:"recurseCount", title: '<i class="glyphicon glyphicon-repeat" title="Set recurse count"></i>', editable: {
    type: "select",
    mode: "inline",
    value: 1,
  //   source: [
  //     {value: 1, text: 1},
  //     {value: 2, text: 2},
  //     {value: 3, text: 3},
  //     {value: 4, text: 4},
  //     {value: 5, text: 5}
  //     ],
    source: function(){
      var result = [];
      for(var i = 1; i < 21; i++){
        var option = {};
        option.value = i;
        option.text = i;
        result.push(option);
      }
      return result;
    },
    align: "center"}
  });
qsCols.push({field:"addPKRelation", title: '<i class="glyphicon glyphicon-magnet" title="Add PK relation(s)"></i>', formatter: "addPKRelationFormatter", align: "center"});
qsCols.push({field:"addRelation", title: '<i class="glyphicon glyphicon-plus-sign" title="Add new relation"></i>', formatter: "addRelationFormatter", align: "center"});
qsCols.push({field:"addField", title: '<i class="glyphicon glyphicon-plus-sign" title="Add new field"></i>', formatter: "addFieldFormatter", align: "center"});
qsCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeFormatter", align: "center"});
qsCols.push({field:"linker", formatter: "boolFormatter", title: "linker", align: "center"});
qsCols.push({field:"linker_ids", title: "linker_ids"});

var fieldCols = [];
fieldCols.push({field:"index", title: "index", formatter: "indexFormatter", sortable: false});
fieldCols.push({field:"field_name", title: "Name", sortable: true });
fieldCols.push({class:"field_type", field:"field_type", title: "Type", editable: {type: "text", mode: "inline"}, sortable: true});

var customFieldType = {
  type: "select",
  mode: "inline"
};

fieldCols.push({field:"label", title: "label", editable: {type: "text", mode: "inline"}, sortable: true});
fieldCols.push({field:"description", title: "Description", sortable: false, editable: {type: "textarea", mode: "inline", rows: 4}});
fieldCols.push({field:"expression", title: "Expression", sortable: false, editable: {type: "textarea", mode: "inline", rows: 4}});
// fieldCols.push({field:"traduction", title: "traduction", formatter: "boolFormatter", align: "center", sortable: false});
fieldCols.push({field:"hidden", title: "Hidden", formatter: "boolFormatter", align: "center", sortable: false});
// fieldCols.push({field:"field_type", title: "field_type", editable: false, sortable: true});
// fieldCols.push({field:"field_size", title: "field_size", editable: false, sortable: true});
// fieldCols.push({field:"nullable", title: "nullable", editable: false, sortable: true});
// fieldCols.push({field:"timezone", title: "timezone", formatter: "boolFormatter", align: "center", sortable: false});
fieldCols.push({field:"icon", title: "Icon", editable:{
  type: "select",
  mode: "inline",
  value: "Attribute",
  source: [{value: "Attribute", text: "Attribute"}, {value: "Identifier", text: "Identifier"}, {value: "Fact", text: "Fact"}]
  }
});
fieldCols.push({field:"displayType", title: "DisplayType", editable:{
  type: "select",
  mode: "inline",
  value: "Value",
  source: [{value: "Link", text: "Link"}, {value: "Picture", text: "Picture"}, {value: "Value", text: "Value"}]
  }
});

var measure = {
  type: "select",
  mode: "inline",
  value: [],
  source: [
      {value: '', text: ''},
      {value: 'Average', text: 'Average'},
      {value: 'Count', text: 'Count'},
      {value: 'Count Distinct', text: 'Count Distinct'},
      {value: 'Sum', text: 'Sum'},
      {value: 'Maximun', text: 'Maximun'},
      {value: 'Minimum', text: 'Minimum'},
      {value: 'Median', text: 'Median'},
      {value: 'Standard Deviation', text: 'Standard Deviation'},
      {value: 'Variance', text: 'Variance'}
  ]
};

fieldCols.push({field:"measure", title: "Measure", editable: measure});

var dateDimensions = {
  type: "checklist",
  mode: "inline",
  value: [],
  source: [
    {value: 'Year', text: 'Year'},
    {value: 'Quarter', text: 'Quarter'},
    {value: 'Month', text: 'Month'},
    {value: 'Weeks', text: 'Weeks'},
    {value: 'Day', text: 'Day'},
    {value: 'AM/PM', text: 'AM/PM'},
    {value: 'Hour', text: 'Hour'},
    {value: 'Min', text: 'Min'},
    {value: 'Date', text: 'Date'}
  ]
}

var dimensions = {
  type: "checklist",
  mode: "inline",
  value: [0],
  source: [
    {value: 0, text: 'value 0'},
    {value: 1, text: 'value 1'},
    {value: 2, text: 'value 2'},
    {value: 3, text: 'value 3'}
  ]
}

fieldCols.push({field:"dimension", title: "Dimension", editable: {type: 'text', mode: 'inline'}});
fieldCols.push({field:"order", title: "Order", editable: {type: "text", mode: "inline"}, sortable: true});
fieldCols.push({field:"bk", title: "BK", editable: {type: "text", mode: "inline"}, sortable: true});
fieldCols.push({field:"hierarchyName", title: "Hierarchy Name", editable: {type: "text", mode: "inline"}, sortable: true});

fieldCols.push({field:"buildDrillPath", title: '<i class="glyphicon glyphicon-zoom-in"></i>', formatter: "buildDrillPathFormatter", align: "center"});
fieldCols.push({field:"remove", title: '<i class="glyphicon glyphicon-trash"></i>', formatter: "removeFormatter", align: "center"});

$(document)
.ready(function() {
  // localStorage.setItem('dbmd', null);
  GetDBMDFromCache();

  buildTable($datasTable, qsCols, datas, true);
  GetCognosLocales
  GetDBDataType();
  // buildComboList($('#selectDimension'));

})
.ajaxStart(function(){
    $("div#divLoading").addClass('show');
		$("div#modDivLoading").addClass('show');
})
.ajaxStop(function(){
    $("div#divLoading").removeClass('show');
		$("div#modDivLoading").removeClass('show');
});

$tableList.change(function () {
    var selectedText = $(this).find("option:selected").val();
		$('#alias').val(selectedText);
});

$tableList.on('show.bs.select', function (e) {
  // do something...
  // console.log("$tableList.on('show.bs.select'");
  // ChooseTable($tableList);

});

// $navTab.on('shown.bs.tab', function(event){
//     activeTab = $(event.target).text();         // active tab
// 		console.log("Event shown.bs.tab: activeTab=" + activeTab);
//     previousTab = $(event.relatedTarget).text();  // previous tab
// 		console.log("Event shown.bs.tab: previousTab=" + previousTab);
// });

$('#pktable_alias').on('save', function(e, params) {
    alert('Saved value: ' + params.newValue);
});

$navTab.on('show.bs.tab', function(event){
    activeTab = $(event.target).text();         // active tab
		console.log("Event show.bs.tab: activeTab=" + activeTab);
    previousTab = $(event.relatedTarget).text();  // previous tab
		console.log("Event show.bs.tab: previousTab=" + previousTab);
});

$qsTab.on('shown.bs.tab', function(e) {
  buildTable($datasTable, qsCols, datas, true, fieldCols, "fields");
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable('showColumn', 'visible');
  $datasTable.bootstrapTable('showColumn', 'filter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('hideColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addPKRelation');
  $datasTable.bootstrapTable('showColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'recurseCount');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'above');
  $datasTable.bootstrapTable('showColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'remove');
});

$finTab.on('shown.bs.tab', function(e) {
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable("filterBy", {type: ['Final']});
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('hideColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('hideColumn', 'addPKRelation');
  $datasTable.bootstrapTable('hideColumn', 'nommageRep');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('showColumn', 'remove');
});

$refTab.on('shown.bs.tab', function(e) {
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  // $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref']});
  $datasTable.bootstrapTable("filterBy", {});

  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('showColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('showColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'nommageRep');
  $datasTable.bootstrapTable('hideColumn', '_id');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'remove');
});

$secTab.on('shown.bs.tab', function(e) {
  buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  // $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref', 'Sec']});
  $datasTable.bootstrapTable("filterBy", {});
  $datasTable.bootstrapTable('showColumn', 'operate');
  $datasTable.bootstrapTable('hideColumn', 'visible');
  $datasTable.bootstrapTable('hideColumn', 'filter');
  $datasTable.bootstrapTable('showColumn', 'label');
  $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  $datasTable.bootstrapTable('showColumn', 'addRelation');
  $datasTable.bootstrapTable('hideColumn', 'addField');
  $datasTable.bootstrapTable('showColumn', 'recurseCount');
  $datasTable.bootstrapTable('showColumn', 'nommageRep');
  $datasTable.bootstrapTable('hideColumn', 'above');
  $datasTable.bootstrapTable('hideColumn', 'folder');
  $datasTable.bootstrapTable('hideColumn', 'linker');
  $datasTable.bootstrapTable('hideColumn', 'linker_ids');

});

$traTab.on('shown.bs.tab', function(e) {
  // buildTable($datasTable, qsCols, datas, true, relationCols, "relations");
  // $datasTable.bootstrapTable("filterBy", {type: ['Final', 'Ref', 'Tra']});
  // $datasTable.bootstrapTable('showColumn', 'operate');
  // $datasTable.bootstrapTable('hideColumn', 'visible');
  // $datasTable.bootstrapTable('hideColumn', 'filter');
  // $datasTable.bootstrapTable('showColumn', 'label');
  // $datasTable.bootstrapTable('showColumn', 'addPKRelation');
  // $datasTable.bootstrapTable('showColumn', 'addRelation');
  // $datasTable.bootstrapTable('showColumn', 'recurseCount');
  // $datasTable.bootstrapTable('showColumn', 'nommageRep');
});


// $datasTable.on('editable-save.bs.table', function (editable, field, row, oldValue, $el) {
//   console.log("row");
//   console.log(row);
//   console.log("$el");
//   console.log($el);
//   row._id = row.key_type + 'K_' + row.pktable_alias + '_' + row.table_alias + '_' + row.type;
//   if(field == "pktable_alias"){
//     var newValue = row.pktable_alias;
//     if($activeSubDatasTable != undefined){
//       updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.split("[" + oldValue + "]").join("[" + newValue + "]"));
//     }
//   }
// });

$datasTable.on('resetrr-view.bs.table', function(){
  // console.log("++++++++++++++on passe dans reset-view");
  // console.log("activeTab=" + activeTab);
  // console.log("previousTab=" + previousTab);
  if($activeSubDatasTable != undefined){
    var v = $activeSubDatasTable.bootstrapTable('getData');
    // console.log("+++++++++++ $activeSubDatasTable");
    // console.log(v);
    var $tableRows = $activeSubDatasTable.find('tbody tr');
    // console.log("++++++++++ $tableRows");
    // console.log($tableRows);
    $.each(v, function(i, row){
      // console.log("row.ref");
      // console.log(row.ref);
      // console.log("row.fin");
      // console.log(row.fin);

      // Disable RepTableName if !ref
      if(activeTab == "Reference" && !row.ref){
        $tableRows.eq(i).find('a').eq(4).editable('disable');
        // $tableRows.eq(i).find('a').editable('disable');
      }
      if(activeTab == "Security" && !row.sec){
        $tableRows.eq(i).find('a').eq(4).editable('disable');
        // $tableRows.eq(i).find('a').editable('disable');
      }
      if(row.fin || row.ref || row.sec){
        $tableRows.eq(i).find('a').eq(0).editable('disable');
        // $tableRows.eq(i).find('a').editable('disable');
      }
      if(row.fin && activeTab.match("Reference|Security")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      if(row.ref && activeTab.match("Final|Security")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      if(row.sec && activeTab.match("Final|Reference")){
        $tableRows.eq(i).find('a').eq(3).editable('disable');
        $tableRows.eq(i).find('a').eq(2).editable('disable');
      }
      // if(activeTab.match("Query Subject") && row.field_type != undefined){
      //   if(row.field_type.toUpperCase() == "DATE" || row.field_type.toUpperCase() == "TIMESTAMP" || row.field_type.toUpperCase() == "DATETIME"){
      //     $tableRows.eq(i).find('a').eq(8).editable('destroy');
      //     $tableRows.eq(i).find('a').eq(8).editable(dateDimensions);
      //   // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dateDimensions.source);
      //   }
      //   else{
      //     // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dimensions.source);
      //   }
      // }
      if(activeTab.match("Query Subject")){
        if(row.custom != true){
          $tableRows.eq(i).find('a').eq(0).editable('disable');
          // console.log($tableRows.eq(i).find('a.remove').val('hidden'));
          // console.log(row);
          // $tableRows.eq(i).find('a.remove').prop("disabled",true);
          $tableRows.eq(i).find('a.remove').remove();
        }
        else{
          $tableRows.eq(i).find('a').eq(0).editable('destroy');
          // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);
          customFieldType.source = dbDataType;
          $tableRows.eq(i).find('a').eq(0).editable(customFieldType);
          $tableRows.eq(i).find('a').eq(0).editable('option', 'defaultValue', '');
        }
      }

    });
  }
});

$datasTable.on('expand-row.bs.table', function (index, row, $detail) {
  // console.log("index: ");
  // console.log(index);
  // console.log("row: ");
  // console.log(row);
  // console.log("$detail: ");
  // console.log($detail);

});

$newRowModal.on('show.bs.modal', function (e) {
  // do something...
	// ChooseQuerySubject($('#modQuerySubject'));
  $('#modRelationship').val('');
  $('#modPKTable').empty();
  $('#modPKColumn').empty();
  $('#modKeyType').empty();
  $('#modPKColumn').selectpicker('refresh');

  if(activeTab == "Final"){
    $('#modKeyType').append('<option value="F">Foreign</option>');
  }
  if(activeTab.match("Reference|Security")){
    $('#modKeyType').append('<option value="F">Foreign</option>');
    $('#modKeyType').append('<option value="P">Primary</option>');
  }
  $('#modKeyType').selectpicker('refresh');

  // var tables = JSON.parse(localStorage.getItem('tables'));
  // $.each(tables, function(i, obj){
  //   var option = '<option class="fontsize" value=' + obj.name + '>' + obj.name + ' (' + obj.remarks + ') (' + obj.FKCount + ') (' + obj.FKSeqCount + ')'
  //    + ' (' + obj.PKCount + ') (' + obj.PKSeqCount + ') (' + obj.RecCount + ')' + '</option>';
  //   $('#modPKTables').append(option);
  // });
  // $('#modPKTables').selectpicker('refresh');
	ChooseTable($('#modPKTable'));
  // $(this)
  // .find('.modal-body')
  // .load("sqel.html", function(){
  //});

})

$modelListModal.on('shown.bs.modal', function() {
  $(this).find('.modal-body').empty();
  var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
  list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
  list += '<div id="searchlist" class="list-group">';

  $.each(modelList, function(index, object){
    list += '<a href="#" class="list-group-item" onClick="OpenModel(' + object.id + '); return false;"><span>' + object.name + '</span></a>';
  });
  list += '</div></form><script>$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false});</script>';
  $(this).find('.modal-body').append(list);
});


$projectFileModal.on('shown.bs.modal', function() {
    $(this).find('.modal-body').empty();
    var html = [
      '<div class="container-fluid"><div class="row"><div class="form-group"><div class="input-group">',
	'<span class="input-group-addon">model-</span>',
      '<input type="text" id="filePath" class="form-control">',
      '</div></div></div></div>',
    ].join('');

    $(this).find('.modal-body').append(html);
    $(this).find('#filePath').focus().val("NNN");


});

$('#modPKTable').change(function () {
    var selectedText = $(this).find("option:selected").val();
    ChooseField($('#modPKColumn'), selectedText);
});

window.operateRelationEvents = {
    'click .duplicate': function (e, value, row, index) {
      console.log("+++++ on entre dans click .duplicate");
      console.log(e);
      console.log(value);
      console.log(row);
      console.log(index);

        nextIndex = row.index + 1;
        console.log("nextIndex=" + nextIndex);
        var newRow = $.extend({}, row);
        newRow.checkbox = false;
        newRow.pktable_alias = "";
        newRow.fin = false;
        newRow.ref = false;
        newRow.relationship = newRow.relationship.replace(/ = \[FINAL\]\./g, " = ");
        newRow.relationship = newRow.relationship.replace(/ = \[REF\]\./g, " = ");
        console.log("newRow");
        console.log(newRow);
        $activeSubDatasTable.bootstrapTable('insertRow', {index: nextIndex, row: newRow});
        console.log("+++++ on sort de click .duplicate");

    },
    'click .remove': function (e, value, row, index) {
        $activeSubDatasTable.bootstrapTable('remove', {
            field: 'index',
            values: [row.index]
        });
    }
};

window.operateQSEvents = {
    'click .addRelation': function (e, value, row, index) {

      console.log("index=" + index);
      // $datasTable.bootstrapTable('expandAllRows');
      $datasTable.bootstrapTable('expandRow', index);

      console.log("++++++++++++++on passe dans window.operateQSEvents.add");
      if($activeSubDatasTable != ""){

        var v = $activeSubDatasTable.bootstrapTable('getData');
        console.log("+++++++++++ $activeSubDatasTable");

        console.log(v);
        $newRowModal.modal('toggle');
        var qs = row.table_alias + ' - ' + row.type + ' - ' + row.table_name;
        // $('#modQuerySubject').selectpicker('val', qs);

        $('#modQuerySubject').text(qs);
        $('#modKeyName').val("CK_" + row.table_alias);
        $('#modPKTableAlias').val("");
        // $('#modRelathionship').val("[" + row.type.toUpperCase() + "].[" + row.table_alias + "].[] = ");
      }

    },
    'click .expandAllQS': function (e, value, row, index) {
      $datasTable.bootstrapTable("expandAllRows")
    },
    'click .collapseAllQS': function (e, value, row, index) {
      $datasTable.bootstrapTable("collapseAllRows")
    }
};

window.aboveEvents = {
  'change .Select1': function (e, value, row, index){
    var selectedText = $("#Select1").find("option:selected").val();
    row.above = selectedText;
    console.log(e);
    console.log(value);
    console.log(row);
    console.log(index);
    console.log($activeSubDatasTable);
    // updateCell($activeSubDatasTable, index, 'above', selectedText);
    // updateRow($activeSubDatasTable, index, row);
  }

};

function buildComboList($el) {

  var content = "<input type='text' class='bss-input' onKeyDown='event.stopPropagation();' onKeyPress='addSelectInpKeyPress(this,event)' onClick='event.stopPropagation()' placeholder='Add item'> <span class='glyphicon glyphicon-plus addnewicon' onClick='addSelectItem(this,event,1);'></span>";
  // var content = "<input type='text' class='bss-input' onKeyDown='event.stopPropagation();' onKeyPress='addSelectInpKeyPress(this,event)' onClick='event.stopPropagation()' placeholder='Add item'> <span class='glyphicon glyphicon-plus addnewicon' onClick='addSelectItem(this,event,1);'></span>";

  var divider = $('<option/>')
          .addClass('divider')
          .data('divider', true);


  var addoption = $('<option/>', {class: 'addItem'})
          .data('content', content)

  $el.append(divider)
  $el.append(addoption)
  $el.selectpicker();

};

function addSelectItem(t,ev){
   ev.stopPropagation();

   var bs = $(t).closest('.bootstrap-select')
   var txt=bs.find('.bss-input').val().replace(/[|]/g,"");
   var txt=$(t).prev().val().replace(/[|]/g,"");
   if ($.trim(txt)=='') return;

   // Changed from previous version to cater to new
   // layout used by bootstrap-select.
   var p=bs.find('select');
   var o=$('option', p).eq(-2);
   o.before( $("<option>", { "selected": true, "text": txt}) );
   p.selectpicker('refresh');
}

function addSelectInpKeyPress(t,ev){
   ev.stopPropagation();

   // do not allow pipe character
   if (ev.which==124) ev.preventDefault();

   // enter character adds the option
   if (ev.which==13)
   {
      ev.preventDefault();
      addSelectItem($(t).next(),ev);
   }
}

$('#selectDimension').change(function () {
    var selectedText = $(this).find("option:selected").val();

    // var option = '<option class="fontsize" value="' + obj.table_name + '" data-subtext="' + obj.table_remarks +  ' ' + obj.table_stats + '">'
    //  + obj.table_name + '</option>';
    // table.append(option);




});

$('#selectDimension').on('show.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("show");

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){

    console.log(obj);

  });

});

$('#selectDimension').on('shown.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("shown");
});

$('#selectDimension').on('hide.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("hide");
});

$('#selectDimension').on('hidden.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("hidden");
  $('#selectOrder').empty();
  $('#selectOrder').append('<option></option>');
  var dimension = $(this).find("option:selected").val();
  console.log(dimension);
  if(dimension != ''){
    $.each($datasTable.bootstrapTable("getData"), function(i, obj){

      console.log(obj);
      $.each(obj.fields, function(j, field){
        if(field.dimension == dimension){
          var option = '<option class="fontsize" value="' + field.field_name + '" data-subtext="' + obj.table_alias + '">' + field.field_name + '</option>';
          $('#selectOrder').append(option);
        }
      });
      $('#selectOrder').selectpicker('refresh');
    });
  }



});

$('#selectDimension').on('loaded.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("loaded");
});

$('#selectDimension').on('rendered.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("rendered");
});

$('#selectDimension').on('refreshed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("refreshed");
});

$('#selectDimension').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
  console.log("changed");
});

function BuildDrillPath(){

  var dimension = $('#selectDimension').find("option:selected").val();
  var order = $('#selectOrder').find("option:selected").val();
  var bk = $('#selectBK').find("option:selected").val();
  var hierarchyName = $('#hierarchyName').val();

  console.log("drillFieldName");
  console.log($('#drillFieldName').text());
  // var alias = $('#drillFieldName').text().split('.')[0];
  var field = $('#drillFieldName').text().split('.')[1];
  console.log(field);


  if($activeSubDatasTable != undefined){
    $.each($activeSubDatasTable.bootstrapTable("getData"), function(i, obj){
      if(obj.field_name == field){
        console.log(obj);
        obj.dimension = dimension;
        if(!order){obj.order = ""} else{obj.order = '[DATA].[' + dimension + '].[' + order + ']';}
        obj.bk = '[DATA].[' + dimension + '].[' + bk + ']';
        obj.hierarchyName = hierarchyName;
        updateRow($activeSubDatasTable, obj.index, obj);
      }

    });

  }
  $('#DrillModal').modal('toggle');

}

function aboveFormatter(value, row, index){

  if(row.seqs.length < 2){
    // row.above = row.seqs[0].column_name;
    return row.seqs[0].column_name;
  }

  else{
    return row.seqs[1].column_name;

    // row.above = row.seqs[0].column_name;
    // var options_str = "";
    // var above = row.above;
    //
    // $.each(row.seqs, function(index, seq){
    //   options_str += '<option value="' + seq.column_name + '">' + seq.column_name + '</option>';
    // });
    //
    // return [
    //   '<select class="Select1" name="drop1" id="Select1">',
    //   options_str,
    //   '</select>'
    // ].join('');
  }
}



function operateRelationFormatter(value, row, index) {
    return [
        '<a class="duplicate" href="javascript:void(0)" title="Duplicate">',
        '<i class="glyphicon glyphicon-duplicate"></i>',
        '</a>  ',
        '<a class="remove" href="javascript:void(0)" title="Remove">',
        '<i class="glyphicon glyphicon-trash"></i>',
        '</a>'
    ].join('');
}

function operateQSFormatter(value, row, index) {
    return [
        '<a class="addRelation" href="javascript:void(0)" title="Add Relation">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>  ',
        '<a class="expandAllQS" href="javascript:void(0)" title="Expand all QS">',
        '<i class="glyphicon glyphicon-resize-full"></i>',
        '</a>  ',
        '<a class="collapseAllQS" href="javascript:void(0)" title="Collapse all QS">',
        '<i class="glyphicon glyphicon-resize-small"></i>',
        '</a>'
    ].join('');
}

function addPKRelationFormatter(value, row, index) {
    return [
        '<a class="addPKRelation" href="javascript:void(0)" title="Add PK relation(s)">',
        '<i class="glyphicon glyphicon-magnet"></i>',
        '</a>'
    ].join('');
}

function addRelationFormatter(value, row, index) {
    return [
        '<a class="addRelation" href="javascript:void(0)" title="Add new relation">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>'
    ].join('');
}

function addFieldFormatter(value, row, index) {
    return [
        '<a class="addRelation" href="javascript:void(0)" title="Add new field">',
        '<i class="glyphicon glyphicon-plus-sign"></i>',
        '</a>'
    ].join('');
}

function boolFormatter(value, row, index) {

  // console.log("****** VALUE *********" + value);
  //
  // if(value == undefined){
  //   value = false;
  // }
  var icon = value == true ? 'glyphicon-check' : 'glyphicon-unchecked'
  if(value == undefined){
      // console.log("****** VALUE *********" + value);
      // console.log(row);
      icon = 'glyphicon-unchecked';
  }
  return [
    '<a href="javascript:void(0)">',
    '<i class="glyphicon ' + icon + '"></i> ',
    '</a>'
  ].join('');
}

function duplicateFormatter(value, row, index) {
  return [
      '<a class="duplicate" href="javascript:void(0)" title="Duplicate">',
      '<i class="glyphicon glyphicon-duplicate"></i>',
      '</a>'
  ].join('');
}

function removeFormatter(value, row, index) {
  return [
      '<a class="remove" href="javascript:void(0)" title="Remove">',
      '<i class="glyphicon glyphicon-trash"></i>',
      '</a>'
  ].join('');
}

function buildDrillPathFormatter(value, row, index) {
  return [
      '<a class="buildDrillPath" href="javascript:void(0)" title="Build Drill Path">',
      '<i class="glyphicon glyphicon-zoom-in"></i>',
      '</a>'
  ].join('');
}

function indexFormatter(value, row, index) {
  row.index = index;
  return index;
}

function modBuildRelation(){

  if(!validNewRelation()){
    return;
  }

  var relations = $('#modRelationship');
  var alias = $('#modQuerySubject').text().split(" - ")[0];
  var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
  var table = $('#modQuerySubject').text().split(" - ")[2];
  var pktable = $('#modPKTable').find("option:selected").val();
  var column = $('#modColumn').find("option:selected").val();
  var pkcolumn = $('#modPKColumn').find("option:selected").val();

  var output = relations.val();
  if(relations.val() != ''){
    output += ' AND ';
  }
  output += '[' + type + '].[' + alias + '].[' + column + '] = [' + pktable + '].[' + pkcolumn + ']';

  relations.val(output);

}

function modAddRelation(){

  var alias = $('#modQuerySubject').text().split(" - ")[0];
  var table = $('#modQuerySubject').text().split(" - ")[2];
  var type = $('#modQuerySubject').text().split(" - ")[1].toUpperCase();
  var key_type = $('#modKeyType').val();
  var relations = $('#modRelationship').val();

  var exp = "\\[" + type + "\\]\.\\[" + alias + "\\]\.\\[([A-Z0-9_]*?)\\][^\\[]*?\\[([A-Z0-9_]*?)\\]\.\\[([A-Z0-9_]*?)\\]";
  // var exp = "\\s{0,}=\\s{0,}\\[(.*?)\\]\.\\[(.*?)\\]";

  var regexp = new RegExp(exp, "gi");

  var match;
  var colMatches = [];
  var pkTabMatches = [];
  var pkColMatches = [];

  while(match = regexp.exec(relations)){
    colMatches.push(match[1]);
    pkTabMatches.push(match[2]);
    pkColMatches.push(match[3]);
  };

  console.log(colMatches);
  console.log(pkTabMatches);
  console.log(pkColMatches);

  if(colMatches.length == 0){
    showalert("modAddRelation()", "No valid relation found in Relations textarea.<br>Relations does not match " + exp + " pattern.", "alert-warning", "bottom");
    return;
  }

  $.ajax({
      type: 'POST',
      url: "GetNewRelation",
      dataType: 'json',

      success: function(data){
          var relation = data;

          $.each(colMatches, function(i, obj){
            var seq = {};
            seq.key_seq = i + 1;
            seq.table_name = table;
            seq.pktable_name = pkTabMatches[i];
            seq.column_name = colMatches[i]
            seq.pkcolumn_name = pkColMatches[i];
            relation.seqs.push(seq);
          })

          $.each(relation.seqs, function(i, seq){
            if(relation.where != ''){
              relation.where += ' AND ';
            }
            relation.where += seq.table_name + '.' + seq.column_name + ' = ' + seq.pktable_name + '.' + seq.pkcolumn_name;
          })

          relation.relationship = relations;
          relation.type = type;
          relation.table_name = table;
          relation.table_alias = alias;
          relation.pktable_name = pkTabMatches[0];
          relation.pktable_alias = pkTabMatches[0];
          relation.key_type = key_type;
          relation.key_name = key_type + 'K_' + alias + '_' + pkTabMatches[0];
          relation._id = relation.key_name + '_' + type;

          console.log(relation);

          modWriteRelation(relation);
      },
      error: function(data) {
          console.log(data);
          showalert("GetNewRelation()", "Error when getting new relation from server.", "alert-warning", "bottom");
      }
  });

}

function modWriteRelation(relation){

  var data = $datasTable.bootstrapTable("getData");

  var qs = $('#modQuerySubject').text().split(" - ")[0] + $('#modQuerySubject').text().split(" - ")[1];

  $.each(data, function(i, obj){
    //console.log(obj.name);
    if(obj._id.match(qs)){
      if(obj.relations.length == 0){
        obj.relations.push(relation);
      }
      console.log(obj);
    }
  });

  if($activeSubDatasTable){
    AddRow($activeSubDatasTable, relation);
  }

  $newRowModal.modal('toggle');

}

function validNewRelation(){

  if ($("#modPKTable").find("option:selected").text() == 'Choose a pktable...') {
    showalert("modValidate()", "No pktable selected.", "alert-warning", "bottom");
    return false;
  }

  if ($("#modPKColumn").find("option:selected").text() == 'Choose a pkcolumn...') {
    showalert("modValidate()", "No pkcolumn selected.", "alert-warning", "bottom");
    return false;
  }

  if ($("#modColumn").find("option:selected").text() == 'Choose a column...') {
    showalert("modValidate()", "No column selected.", "alert-warning", "bottom");
    return false;
  }

  return true;
}

function Search(){
  window.open("search.html");
}

// function getLabel(tableName, columnName){
//
//   // console.log("tableName=" + tableName);
//   // console.log("columnName=" + columnName);
//
//   var label = null;
//
//   var dbmd = JSON.parse(localStorage.getItem('dbmd'));
//   if(dbmd != 'null' && dbmd){
//     if(dbmd[tableName] && !columnName){
//       label = dbmd[tableName].table_remarks;
//     }
//     if(dbmd[tableName] && columnName){
//       if(dbmd[tableName].columns[columnName]){
//         label = dbmd[tableName].columns[columnName].column_remarks;
//       }
//     }
//   }
//   return label;
// }

// function getDescription(tableName, columnName){
//
//   // console.log("tableName=" + tableName);
//   // console.log("columnName=" + columnName);
//
//   var description = null;
//
//   var labels = JSON.parse(localStorage.getItem('dbmd'));
//   if(labels){
//     if(labels[tableName] && !columnName){
//       description = labels[tableName].table_description;
//     }
//     if(labels[tableName] && columnName){
//       if(labels[tableName].columns[columnName]){
//         description = labels[tableName].columns[columnName].column_description;
//       }
//     }
//   }
//   return description;
// }

function getDimensionSet(){
  var dimensionSet = new Set();
  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
    $.each(obj.fields, function(j, field){
      var dimension = field.dimension
      if(dimension != ''){
        if(!field.field_type.toUpperCase().match("DATE|TIMESTAMP|DATETIME")){
          console.log(dimension);
          console.log(field.field_type.toUpperCase());
          dimensionSet.add(dimension);
        }
      }
    })
  })
  return dimensionSet;
}

function expandTable($detail, cols, data, parentData) {
    $subtable = $detail.html('<table></table>').find('table');
    // console.log("expandTable.data=");
    // console.log(data);
    // console.log("expandTable.parentData=");
    // console.log(parentData);

    $activeSubDatasTable = $subtable;
    buildSubTable($subtable, cols, data, parentData);
}

function buildSubTable($el, cols, data, parentData){

  $el.bootstrapTable({
      columns: cols,
      // url: url,
      data: data,
      showToggle: false,
      search: false,
      checkboxHeader: false,
      showColumns: false,
      sortName: "recCountPercent",
      sortOrder: "desc",
      idField: "index",

      onAll: function(name, args){
        //Fires when all events trigger, the parameters contain: name: the event name, args: the event data.
        // console.log("---------- buildSubTable: onAll -------------");
        // console.log("name=" + name);
        // console.log("args=");
        // console.log(args);
        // console.log("---------- buildSubTable: onAll -------------");
      },

      onEditableInit: function(){
        //Fired when all columns was initialized by $().editable() method.
      },
      onEditableShown: function(editable, field, row, $el){
        //Fired when an editable cell is opened for edits.
        // console.log("---------- buildSubTable: onEditableShown -------------");
        // console.log("editable=");
        // console.log(editable);
        // console.log("field=");
        // console.log(field);
        // console.log("row=");
        // console.log(row);
        // console.log("$el=");
        // console.log($el);
        // console.log("---------- buildSubTable: onEditableShown -------------");
      },
      onEditableHidden: function(field, row, $el, reason){
        //Fired when an editable cell is hidden / closed.
      },
      onEditableSave: function (field, row, oldValue, editable) {
        //Fired when an editable cell is saved.
        // console.log("---------- buildSubTable: onEditableSave -------------");
        // console.log("editable=");
        // console.log(editable);
        // console.log("field=");
        // console.log(field);
        // console.log("row=");
        // console.log(row);
        // console.log("oldValue=");
        // console.log(oldValue);
        // console.log("---------- buildSubTable: onEditableSave -------------");

        row._id = row.key_type + 'K_' + row.pktable_alias + '_' + row.table_alias + '_' + row.type;
        if(field == "pktable_alias"){
          var newValue = row.pktable_alias;
          if($activeSubDatasTable != undefined){
            var re = new RegExp("[^.]\\[" + oldValue + "\\]", "gi");
            // updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.split(" [" + oldValue + "]").join(" [" + newValue + "]"));
            updateCell($activeSubDatasTable, row.index, 'relationship', row.relationship.replace(re, " [" + newValue + "]"));
          }
        }

        if(field == "dimension"){
          console.log(row.dimension);
          var dimension = row.dimension;
          if(Array.isArray(dimension)){
            row.dimension = dimension.toString();
          };
          console.log(row.dimension);
        }

      },

      onResetView: function(){

        var $tableRows = $el.find('tbody tr');


        $.each(data, function(i, row){


          if(activeTab.match("Reference") && row.seqs.length > 1){
            $tableRows.eq(i).find('a').eq(5).editable('destroy');
            // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);

            var source = [];
            $.each(row.seqs, function(k, seq){
              var option = {};
              option.text = seq.column_name;
              option.value = seq.column_name;
              source.push(option);
            })

            customFieldType.source = source;


            // customFieldType.source = dbDataType;
            $tableRows.eq(i).find('a').eq(5).editable(customFieldType);
            $tableRows.eq(i).find('a').eq(5).editable('option', 'defaultValue', '');
          }

          if(activeTab == "Reference" && parentData.type == 'Final'){
            console.log(parentData);
            console.log($tableRows.eq(i).find('a'));

            var dimensionSet = getDimensionSet();

            var source = [];
            source.push({"text": "", "value": ""});
            dimensionSet.forEach(function(value){
              var option = {};
              option.text = value;
              option.value = value;
              source.push(option);
            })

            usedForDimensionsSelect.source = source;

            // 7 = usedForDimensions
            $tableRows.eq(i).find('a').eq(7).editable('destroy');
            $tableRows.eq(i).find('a').eq(7).editable(usedForDimensionsSelect);
            $tableRows.eq(i).find('a').eq(7).editable('option', 'defaultValue', '');


          }

          if(activeTab == "Reference" && parentData.type == 'Ref'){
            console.log(parentData);
            console.log($tableRows.eq(i).find('a'));

            var source = [];
            source.push({"text": "", "value": ""});
            source.push({"text": "true", "value": "true"});
            source.push({"text": "false", "value": "false"});

            usedForDimensionsSelect.source = source;

            // 7 = usedForDimensions
            $tableRows.eq(i).find('a').eq(7).editable('destroy');
            $tableRows.eq(i).find('a').eq(7).editable(usedForDimensionsSelect);
            $tableRows.eq(i).find('a').eq(7).editable('option', 'defaultValue', '');


          }

          // Disable RepTableName if !ref
          if(activeTab == "Reference" && !row.ref){
            $tableRows.eq(i).find('a').eq(4).editable('disable');
            // $tableRows.eq(i).find('a').editable('disable');
          }
          if(activeTab == "Security" && !row.sec){
            $tableRows.eq(i).find('a').eq(4).editable('disable');
            // $tableRows.eq(i).find('a').editable('disable');
          }
          if(row.fin || row.ref || row.sec){
            $tableRows.eq(i).find('a').eq(0).editable('disable');
            // $tableRows.eq(i).find('a').editable('disable');
          }
          if(row.fin && activeTab.match("Reference|Security")){
            $tableRows.eq(i).find('a').eq(3).editable('disable');
            $tableRows.eq(i).find('a').eq(2).editable('disable');
          }
          if(row.ref && activeTab.match("Final|Security")){
            $tableRows.eq(i).find('a').eq(3).editable('disable');
            $tableRows.eq(i).find('a').eq(2).editable('disable');
          }
          if(row.sec && activeTab.match("Final|Reference")){
            $tableRows.eq(i).find('a').eq(3).editable('disable');
            $tableRows.eq(i).find('a').eq(2).editable('disable');
          }
          if(activeTab.match("Query Subject") && row.field_type != undefined){
            if(row.field_type.toUpperCase() == "DATE" || row.field_type.toUpperCase() == "TIMESTAMP" || row.field_type.toUpperCase() == "DATETIME"){
              $tableRows.eq(i).find('a').eq(8).editable('destroy');
              $tableRows.eq(i).find('a').eq(8).editable(dateDimensions);
              console.log($tableRows.eq(i).find('a.buildDrillPath'));
              $tableRows.eq(i).find('a.buildDrillPath').remove();
            // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dateDimensions.source);
            }
            else{
              // $tableRows.eq(i).find('a').eq(6).editable('option', 'source', dimensions.source);
            }
          }
          if(activeTab.match("Query Subject")){

            // console.log($tableRows.eq(i).find('a'));

            // console.log($tableRows.eq(i).find('a').length);

            // for(href = 0; href < $tableRows.eq(i).find('a').length; href++){
              // console.log($tableRows.eq(i).find('a').eq(href));
            // }

            if(row.custom != true){
              $tableRows.eq(i).find('a').eq(0).editable('disable');
              // console.log($tableRows.eq(i).find('a.remove').val('hidden'));
              // console.log(row);
              // $tableRows.eq(i).find('a.remove').prop("disabled",true);
              $tableRows.eq(i).find('a.remove').remove();
            }
            else{
              $tableRows.eq(i).find('a').eq(0).editable('destroy');
              // $tableRows.eq(i).find('a').eq(0).editable('setValue', ['VARCHAR']);
              customFieldType.source = dbDataType;
              $tableRows.eq(i).find('a').eq(0).editable(customFieldType);
              $tableRows.eq(i).find('a').eq(0).editable('option', 'defaultValue', '');
            }
          }

        })

      },

      onClickCell: function (field, value, row, $element){

        $activeSubDatasTable = $el;

        switch(field){

          case "dimension":
            console.log("---------- buildSubTable: case dimension -------------");
            console.log("field=");
            console.log(field);
            console.log("value=");
            console.log(value);
            console.log("row=");
            console.log(row);
            console.log("$element=");
            console.log($element);
            console.log("---------- buildSubTable: case dimension -------------");

            // if(row.field_type == "DATE"){
            //   $element.options.source = dateDimensions;
            //   console.log("!!!!!!!!!!!!!!!!!!!!!!!");
            //   console.log($(this).bootstrapTable('getOptions'));
            //   var col = $(this).bootstrapTable('getOptions')[0].columns[0];
            //   var src = col[8].editable.source
            //   console.log(src);
            //   $(this).bootstrapTable('getOptions')[0].columns[0][8].editable.source = dateDimensions;
            //   // $(this).bootstrapTable('refreshOptions', $(this).bootstrapTable('getOptions'));
            // }

            break;

          case "buildDrillPath":

            if(row.field_type.toUpperCase() == "DATE" || row.field_type.toUpperCase() == "TIMESTAMP" || row.field_type.toUpperCase() == "DATETIME"){
              return;
            }

            console.log(row);
            console.log(parentData);
            var fieldName = parentData.table_alias + '.' + row.field_name;
            $('#drillFieldName').text(fieldName);
            $('#selectDimension').empty();
            $('#selectOrder').empty();
            $('#selectBK').empty();
            ChooseField($('#selectBK'), parentData.table_name);

            var dimensionSet = getDimensionSet();

            dimensionSet.forEach(function(value){
              $('#selectDimension').append('<option>' + value + '</option>');
            })
            $('#selectDimension').selectpicker('refresh');
            $('#selectOrder').selectpicker('refresh');

            $('#DrillModal').modal('toggle');

            break;

          case "traduction":
          case "hidden":
          case "timezone":
          case "leftJoin":
          // case "usedForDimensions":
          case "rightJoin":
            console.log(field);
            console.log(value);
            console.log(row);
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);
            break;

          case "nommageRep":
            var allowNommageRep = true;

            if(!row.ref && activeTab.match("Reference")){
              allowNommageRep = false;
              showalert("buildSubTable()", "Ref for " + row.pktable_alias + " has to be checked first.", "alert-warning", "bottom");
              return;
            }
            if(!row.sec && activeTab.match("Security")){
              allowNommageRep = false;
              showalert("buildSubTable()", "Sec for " + row.pktable_alias + " has to be checked first.", "alert-warning", "bottom");
              return;
            }

            if(value == false){
              console.log(allowNommageRep);
              // interdire de cocher n fois pour un même pkAlias dans un qs donné
              $.each($el.bootstrapTable("getData"), function(i, obj){
                if(obj.pktable_alias == row.pktable_alias){
                  console.log(obj);
                  console.log(obj.pktable_alias + " -> " + obj.nommageRep);
                  console.log(row);
                  console.log(row.pktable_alias + " -> " + row.nommageRep);
                  if(obj.sec && activeTab.match("Security") && obj.nommageRep){
                    allowNommageRep = false;
                  }
                  if(obj.ref && activeTab.match("Reference") && obj.nommageRep){
                    allowNommageRep = false;
                  }
                }
              });

            }
            if(!allowNommageRep){
              showalert("buildSubTable()", "RepTableName for pktable_alias " + row.pktable_alias + " already checked.", "alert-warning", "bottom");
              return;
            }
            var newValue = value == false ? true : false;
            updateCell($el, row.index, field, newValue);

            break;

          case "duplicate":

            $el.bootstrapTable("filterBy", {});
            nextIndex = row.index + 1;
            console.log("nextIndex=" + nextIndex);
            var newRow = $.extend({}, row);
            newRow.checkbox = false;
            newRow.pktable_alias = "";
            newRow.fin = false;
            newRow.ref = false;
            newRow.sec = false;
            newRow.tra = false;
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[FINAL\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[REF\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[SEC\]\./g, " = ");
            newRow.relationship = newRow.relationship.replace(/\s{1,}=\s{1,}\[TRA\]\./g, " = ");
            newRow.relationship = newRow.relationship.split("[" + row.pktable_alias + "]").join("[]");
            newRow.nommageRep = false;
            if(newRow.key_type == "F"){
              newRow._id = "FK_" + newRow.pktable_alias + "_" + row.table_alias + '_' +row.type;
              // newRow._id = newRow.key_name + "F";
            }
            if(newRow.key_type == "P"){
              newRow._id = "PK_" + newRow.pktable_alias + "_" + row.table_alias + '_' +row.type;
              // newRow._id = newRow.key_name + "P";
            }
            console.log("newRow");
            console.log(newRow);
            $el.bootstrapTable('insertRow', {index: nextIndex, row: newRow});
            return;

          case "remove":
            if(activeTab.match("Final|Reference|Security")){
              if(!row.fin && !row.ref){
                $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                });
              }
              else{
                showalert("buildSubTable()", row._id + " is checked.", "alert-warning", "bottom");
              }
            }
            if(activeTab.match("Query Subject")){
              if(row.custom == true){
                $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                });
              }
            }
            return;

          case "fin":
          case "ref":
          case "sec":

            console.log(row);
            console.log(value);

            if(row.ref && activeTab.match("Final|Security")){
              showalert("buildSubTable()", row._id + " is already checked as REF.", "alert-warning", "bottom");
              return;
            }
            if(row.fin && activeTab.match("Reference|Security")){
              showalert("buildSubTable()", row._id + " is already checked as FINAL.", "alert-warning", "bottom");
              return;
            }
            if(row.sec && activeTab.match("Reference|Final")){
              showalert("buildSubTable()", row._id + " is already checked as SEC.", "alert-warning", "bottom");
              return;
            }
            if(row.pktable_alias == ""){
              showalert("buildSubTable()", "Empty is not a valid pktable_alias.", "alert-warning", "bottom");
              return;
            }
            var newValue = value == false ? true : false;
            var pkAlias = '[' + row.pktable_alias + ']';
            console.log("pkAlias=" + pkAlias);
            console.log(newValue);
            if(value == true){
              console.log('++++++PrepareRemoveKey');
              PrepareRemoveKeys(row, parentData);
              console.log('++++++');
              console.log(qs2rm);
              if(qs2rm.qsList.length > 0){

                RemoveKeys(row, parentData);
                ChangeIcon(row, parentData, "Attribute");
                return;
              }
              else{
                if(activeTab == "Final"){
                  row.relationship = row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
                  row.fin = false;
                }
                if(activeTab == "Reference"){
                  row.relationship = row.relationship.split("[REF]." + pkAlias).join(pkAlias);
                  row.ref = false;
                }
                if(activeTab == "Security"){
                  row.relationship = row.relationship.split("[SEC]." + pkAlias).join(pkAlias);
                  row.sec = false;
                }

                var linked = false;
                $.each(parentData.relations, function(i, obj){
                  if(obj.fin || obj.ref || obj.sec){
                    linked = true;
                  }
                });
                updateCell($datasTable, parentData.index, "linker", linked);

              }
            }
            if(value == false){

              var re = new RegExp("[^\\.]\\[" + row.pktable_alias + "\\]", "gi");

              if(!row.fin && activeTab == "Final"){
                row.relationship = row.relationship.replace(re, " [FINAL].[" + row.pktable_alias + "]");
              }
              if(!row.ref && activeTab == "Reference"){
                row.relationship = row.relationship.replace(re, " [REF].[" + row.pktable_alias + "]");
              }
              if(!row.ref && activeTab == "Security"){
                row.relationship = row.relationship.replace(re, " [SEC].[" + row.pktable_alias + "]");
              }
              updateCell($el, row.index, field, newValue);
              ChangeIcon(row, parentData, "Identifier");
              if(row.fin && activeTab == "Final"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Final", row._id, parentData.index);
              }
              if(row.ref && activeTab == "Reference"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Ref", row._id, parentData.index);
              }
              if(row.sec && activeTab == "Security"){
                GetQuerySubjects(row.pktable_name, row.pktable_alias, "Sec", row._id, parentData.index);
              }
              updateCell($datasTable, parentData.index, "linker", true);
//              $datasTable.bootstrapTable('collapseRow', parentData.index);
  //            $datasTable.bootstrapTable('expandRow', parentData.index);

            }
            var linked = false;
            $.each(parentData.relations, function(i, obj){
              if(obj.fin || obj.ref || obj.sec){
                linked = true;
              }
            });
            updateCell($datasTable, parentData.index, "linker", linked);

            break;

          default:

            // console.log(row);
            // console.log(value);
            // console.log(newValue);

        }

      }

  });

  // $el.bootstrapTable('hideColumn', '_id');

  if(activeTab == "Reference"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('showColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('showColumn', 'usedForDimensions');
    $el.bootstrapTable('showColumn', 'rightJoin');
    $el.bootstrapTable('showColumn', 'above');
  }

  if(activeTab == "Security"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'sec');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('showColumn', 'rightJoin');
    $el.bootstrapTable('hideColumn', 'above');
  }

  if(activeTab == "Translation"){
    $el.bootstrapTable('hideColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('showColumn', 'tra');
    $el.bootstrapTable('showColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('hideColumn', 'rightJoin');
    $el.bootstrapTable('hideColumn', 'above');
  }

  if(activeTab == "Final"){
    $el.bootstrapTable("filterBy", {key_type: 'F'});
    $el.bootstrapTable('hideColumn', 'ref');
    $el.bootstrapTable('hideColumn', 'sec');
    $el.bootstrapTable('hideColumn', 'tra');
    $el.bootstrapTable('showColumn', 'fin');
    $el.bootstrapTable('hideColumn', 'nommageRep');
    $el.bootstrapTable('hideColumn', 'above');
    $el.bootstrapTable('hideColumn', 'usedForDimensions');
    $el.bootstrapTable('showColumn', 'rightJoin');
  }

}

function ChangeIcon(row, qs, icon){
  $.each(row.seqs, function(i, seq){
    var column_name = seq.column_name;
    $.each(qs.fields, function(j, field){
      if(field.field_name == column_name){
        field.icon = icon;
      }
    })
  })
}

$("#removeKeysModal").on('hidden.bs.modal', function (e) {
  // do something...
  console.log("removeKeysModal hidden event %%%%%%%%%%")
  if(qs2rm != undefined && $activeSubDatasTable != undefined){
    console.log("qs2rm.row.index="+qs2rm.row.index);
    console.log("qs2rm.row.fin="+qs2rm.row.fin);
    var pkAlias = '[' + qs2rm.row.pktable_alias + ']';
    if(activeTab == "Final"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[FINAL]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "fin", true);
    }
    if(activeTab == "Reference"){
      qs2rm.row.relationship = qs2rm.row.relationship.split(pkAlias).join("[REF]." + pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "ref", true);
    }
  }
})

function PrepareRemoveKeys(o, qs){

        // RemoveFilter();
        // console.log($datasTable.bootstrapTable("getOptions"))
        $datasTable.bootstrapTable("filterBy", {});
        var indexes2rm = [];
        var row = o;
        var ids2rm = {};

        var recurse = function(o){
                var tableData = $datasTable.bootstrapTable("getData");
                tableData.forEach(function(e){
                        if(e.linker_ids.indexOf(o._id) > -1){
                                console.log("RemoveKeys _id found: " + o._id + " in QS " + e._id );
                                console.log("RemoveKeys linker_ids.length for QS " + e._id + " is: " + e.linker_ids.length);
                                if(e.linker_ids.length == 1){
                                  console.log("RemoveKeys linker_ids for QS " + e._id + " == 1");
                                  console.log("RemoveKeys linker_ids for QS " + e._id + " relations:");
                                  $.each(e.relations, function(k, v){
                                    console.log("k=" + k);
                                    console.log("v=");
                                    console.log(v);
                                    if(v.fin || v.ref || v.sec){
                                      console.log("RemoveKeys: " + v._id + " is checked. Recurse...");
                                      return recurse(v);
                                    }
                                  });
                                  indexes2rm.push(e._id);
                                  console.log("RemoveImportedKeys push to indexes2rm: " + e._id);
                                }
                                if(e.linker_ids.length > 1){
                                        ids2rm[e._id] = ids2rm[e._id] || [];
                                        ids2rm[e._id].push(o._id);
                                        e.linker_ids.splice(e.linker_ids.indexOf(o._id), 1);
                                        var newValue = e.linker_ids;
                                        console.log("newValue=" + newValue);
                                        // updateCell($datasTable, e.index, "linker_ids", newValue);
                                        console.log("RemoveImportedKeys remove from linker_ids: " + e._id);
                                }
                                // return recurse(tableData[e.index]);
                        }
                        else {
                                return;
                        }
                });
        };
        recurse(o);

        qs2rm.qs = qs;
        qs2rm.row = row;
        qs2rm.qsList = indexes2rm;
        qs2rm.ids2rm = ids2rm;

        console.log(qs2rm);

        // ApplyFilter();
}

function RemoveKeys(row, qs){

  var list = '<ul class="list-group">';
  $.each(qs2rm.qsList, function(i, qs){
    list += '<li class="list-group-item">' + qs + '</li>';
  });
  list += '</ul>';

  bootbox.confirm({
    title: "Following Query Subject will be dropped: ",
    message: list,
    buttons: {
      cancel: {
          label: '<span class="glyphicon glyphicon-remove aria-hidden="true">',
          className: 'btn btn-default'
      },
      confirm: {
          label: '<span class="glyphicon glyphicon-ok aria-hidden="true">',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        var pkAlias = '[' + row.pktable_alias + ']';
        if(activeTab == "Final"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
          qs2rm.row.fin = false;
        }
        if(activeTab == "Reference"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
          qs2rm.row.ref = false;
        }
        if(activeTab == "Security"){
          qs2rm.row.relationship = qs2rm.row.relationship.split("[SEC]." + pkAlias).join(pkAlias);
          qs2rm.row.sec = false;
        }

        $datasTable.bootstrapTable('remove', {
          field: '_id',
          values: qs2rm.qsList
        });

        var linked = false;
        $.each(qs.relations, function(i, obj){
          if(obj.fin || obj.ref || obj.sec){
            linked = true;
          }
        });
        updateCell($datasTable, qs.index, "linker", linked);

      }
    }
  });

}

function RemoveKeysAccepted(){
  if(qs2rm != undefined && $activeSubDatasTable != undefined){
    console.log("qs2rm.row.index="+qs2rm.row.index);
    console.log("qs2rm.row.fin="+qs2rm.row.fin);
    var pkAlias = '[' + qs2rm.row.pktable_alias + ']';
    if(activeTab == "Final"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[FINAL]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "fin", false);
    }
    if(activeTab == "Reference"){
      // qs2rm.row.relationship = qs2rm.row.relationship.split("[REF]." + pkAlias).join(pkAlias);
     updateCell($activeSubDatasTable, qs2rm.row.index, "ref", false);
    }

    var linked = false;
    $.each(qs2rm.qs.relations, function(i, obj){
      if(obj.fin || obj.ref){
        linked = true;
      }
    });
    updateCell($datasTable, qs2rm.qs.index, "linker", linked);

    $datasTable.bootstrapTable('remove', {
      field: '_id',
      values: qs2rm.qsList
    });
    qs2rm.removed = true;

  }
  $("#removeKeysModal").modal('toggle');
}

function buildTable($el, cols, data) {

    $el.bootstrapTable({
        columns: cols,
        // url: url,
        // data: data,
        search: false,
				showRefresh: false,
				showColumns: false,
				showToggle: false,
				pagination: false,
				showPaginationSwitch: false,
        idField: "index",
				// toolbar: "#DatasToolbar",
        detailView: true,

        onResetView: function(){

          var $tableRows = $el.find('tbody tr');

          $.each($el.bootstrapTable("getData"), function(i, row){

            if(!activeTab.match("Final")){
              $tableRows.eq(i).find('a.remove').remove();
            }
            else{
              if(row.linker_ids){
                if(row.linker_ids[0].match("Root") && !row.linker){
                }
                else{
                  $tableRows.eq(i).find('a.remove').remove();
                }
              }
            }

          })

        },

        onClickCell: function (field, value, row, $element){

          // RemoveFilter();

          if(activeTab.match("Final")){
            if(field.match("remove")){

              if(row.linker_ids){
                if(row.linker_ids[0].match("Root") && !row.linker){
                  $el.bootstrapTable('remove', {
                    field: 'index',
                    values: [row.index]
                  });

                }
              }
            }
          }

          if(field == "visible"){
            var newValue = value == false ? true : false;

            console.log($el.bootstrapTable("getData"));

            console.log("row.index=" + row.index);
            console.log("field=" + field);
            console.log("newValue=" + newValue);

            updateCell($el, row.index, field, newValue);

          }

          if(field.match("addRelation")){
            $el.bootstrapTable("collapseAllRows")
            $el.bootstrapTable('expandRow', row.index);

            if($activeSubDatasTable != undefined){
              $newRowModal.modal('toggle');
              console.log(row);
              var qs = row.table_alias + ' - ' + row.type + ' - ' + row.table_name + ' - ' + row.label;
              // $('#modQuerySubject').selectpicker('val', qs);

              $('#modQuerySubject').text(qs);
              ChooseField($('#modColumn'), row._id);
            }
          }
          if(field.match("addPKRelation")){
            $el.bootstrapTable('expandRow', row.index);
            GetPKRelations(row.table_name, row.table_alias, row.type);
          }

          if(field.match("addField")){
            $el.bootstrapTable("collapseAllRows")
            $el.bootstrapTable('expandRow', row.index);

            if($activeSubDatasTable != undefined){
              GetNewField($activeSubDatasTable);
            }
          }


        },
        onExpandRow: function (index, row, $detail) {
          if(activeTab.match("Final|Reference|Security|Translation")){
            expandTable($detail, relationCols, row.relations, row);
          }
          else{
            expandTable($detail, fieldCols, row.fields, row);
          }
        }
    });

    $el.bootstrapTable('hideColumn', 'visible');
    $el.bootstrapTable('hideColumn', 'filter');
    $el.bootstrapTable('showColumn', 'label');
    $el.bootstrapTable('hideColumn', 'recurseCount');
    $el.bootstrapTable('hideColumn', 'addPKRelation');
    $el.bootstrapTable('hideColumn', 'addField');
    $el.bootstrapTable('showColumn', '_id');
    $el.bootstrapTable('showColumn', 'linker');
    $el.bootstrapTable('showColumn', 'linker_ids');
    $el.bootstrapTable('hideColumn', 'linker');
    $el.bootstrapTable('hideColumn', 'linker_ids');


    console.log("in buildTable: activeTab="+activeTab);
    console.log("in buildTable: previousTab="+previousTab);

    if(activeTab == "Reference"){
    }

    if(activeTab == "Final"){
    }

    if(activeTab == "Query Subject"){
    }

    // ApplyFilter();

}

function GetNewField($el) {

  var fieldName;
  var rows = $el.bootstrapTable('getData');


  bootbox.prompt({
    size: "small",
    title: "Enter field name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
       var status = 'OK';
      fieldName = result;
      if(!fieldName){
        return;
      }

      $.each(rows, function(index, row){
        if(row.field_name.toUpperCase() == fieldName.toUpperCase()){
          showalert("GetNewField()", fieldName.toUpperCase() + " already exists.", "alert-warning", "bottom");
          status = 'KO'
  				return;
        }
      })
      if(status == 'OK'){
        $.ajax({
          type: 'POST',
          url: "GetNewField",
          dataType: 'json',

          success: function(data) {
            console.log(data);
            data.field_name = fieldName.toUpperCase();
            data.custom = true;
            AddRow($el, data);
          },
          error: function(data) {
              console.log(data);
          }
        });
      }

    }
  });


}

function updateCell($table, index, field, newValue){

  console.log($table);
  console.log(index);
  console.log(field);
  console.log(newValue);

  $table.bootstrapTable("updateCell", {
    index: index,
    field: field,
    value: newValue
  });

}

function updateRow($table, index, row){

  $table.bootstrapTable("updateRow", {
    index: index,
    row: row
  });

}

function AddRow($table, row){

  $table.bootstrapTable("filterBy", {});
	nextIndex = $table.bootstrapTable("getData").length;
	console.log("nextIndex=" + nextIndex);
	$table.bootstrapTable('insertRow', {index: nextIndex, row: row});

}

function GetQuerySubjectsWithPK(){
  GetQuerySubjects(null, null, null, true);
}

function GetPKRelations(table_name, table_alias, type){

  var parms = "table=" + table_name + "&alias=" + table_alias + "&type=" + type;

	console.log("calling GetPKRelations with: " + parms);

  $.ajax({
    type: 'POST',
    url: "GetPKRelations",
    dataType: 'json',
    data: parms,

    success: function(data) {
			console.log(data);
			if (data.length == 0) {
				showalert("GetPKRelations()", table_name + " has no PK.", "alert-info", "bottom");
				return;
			}

      if($activeSubDatasTable != undefined){

        var index;
        var datas = $datasTable.bootstrapTable("getData");
        $.each(datas, function(i, obj){
          if(obj._id == table_alias + type){
            index = i;
          }
        })
        var relations = datas[index].relations;

        $.each(data, function(i, obj){
          $datasTable.bootstrapTable("getData")[index].relations.push(obj);
        });

        $datasTable.bootstrapTable("collapseRow", index);
        $datasTable.bootstrapTable("expandRow", index);

      }

  	},
      error: function(data) {
          console.log(data);
          showalert("GetPKRelations()", "Operation failed.", "alert-danger", "bottom");
    }

  });

}

function GetQuerySubjects(table_name, table_alias, type, linker_id, index) {

	var table_name, table_alias, type, linker_id;

  if(linker_id == undefined){
    linker_id = "Root";
  }

	if (table_name == undefined){
		table_name = $tableList.find("option:selected").val();
	}

  console.log("table_name=" + table_name)

  if (table_name == 'Choose a table...' || table_name == '') {
		showalert("GetQuerySubjects()", "No table selected.", "alert-warning", "bottom");
		return;
	}

	if(table_alias == undefined){
		table_alias = $('#alias').val();
	}

	if(type == undefined){
		type = 'Final';
	}

  var qsAlreadyExist = false;

  $.each($datasTable.bootstrapTable("getData"), function(i, obj){
		//console.log(obj.name);
    if(obj._id == table_alias + type){
      qsAlreadyExist = true;
      var newValue = obj.linker_ids;
      newValue.push(linker_id);
      updateCell($datasTable, i, "linker_ids", newValue);

      showalert("GetQuerySubjects()", table_alias + type + " already exists.", "alert-info", "bottom");
    }
  });

  if(qsAlreadyExist){
    return;
  }

	var parms = "table=" + table_name + "&alias=" + table_alias + "&type=" + type + "&linker_id=" + linker_id;

	console.log("calling GetQuerySubjects() with: " + parms);

  $.ajax({
    type: 'POST',
    url: "GetQuerySubjects",
    dataType: 'json',
    data: parms,

    success: function(data) {
			console.log(data);
			if (data[0].relations.length == 0) {
				showalert("GetQuerySubjects()", table_name + " has no key.", "alert-info", "bottom");
				// return;
			}
  		$datasTable.bootstrapTable('append', data);
      datas = $datasTable.bootstrapTable("getData");
      $datasTable.bootstrapTable('expandRow', index);

  	},
      error: function(data) {
          console.log(data);
          showalert("GetQuerySubjects()", "Operation failed.", "alert-danger", "bottom");
    }

  });

}

function RemoveFilter(){
  $datasTable.bootstrapTable("filterBy", {});
  if($activeSubDatasTable != undefined){
    $activeSubDatasTable.bootstrapTable("filterBy", {});
  }
}

function ApplyFilter(){
  if(activeTab == 'Final'){
    $datasTable.bootstrapTable("filterBy", {type: 'Final'});
    if($activeSubDatasTable != undefined){
      $activeSubDatasTable.bootstrapTable("filterBy", {key_type: 'F'});
    }
  }
}

function ChooseQuerySubject(table) {

	table.empty();

  var data = $datasTable.bootstrapTable("getData");

  $.each(data, function(i, obj){
		//console.log(obj.name);
		table.append('<option class="fontsize">' + obj._id + ' - ' + obj.table_name +'</option>');
  });
  table.selectpicker('refresh');

}

function SortOnStats(){

  bootbox.prompt({
      title: "Sort tables list.",
      inputType: 'select',
      inputOptions: [
          {
              text: 'Sort by...',
              value: '',
          },
          {
              text: '...alphabetic table name (ASC).',
              value: '0',
          },
          {
              text: '...number of fields within primary key (DESC).',
              value: '1',
          },
          {
              text: '...number of primary keys imported (DESC).',
              value: '2',
          },
          {
              text: '...number of sequence within primary keys imported (DESC).',
              value: '3',
          },
          {
              text: '...number of foreign keys exported (DESC).',
              value: '4',
          },
          {
              text: '...number of sequence within foreign keys exported (DESC).',
              value: '5',
          },
          {
              text: '...number of indexed fields (DESC).',
              value: '6',
          },
          {
              text: '...number of records (DESC).',
              value: '7',
          }
      ],
      callback: function (result) {
          ChooseTable($tableList, result);
          ChooseTable($('#modPKTable'), result);
      }
  });

}

function ChooseTable(table, sort) {

  table.empty();

  $.ajax({
    type: 'POST',
    url: "GetDBMDFromCache",
    dataType: 'json',
    async: true,
    success: function(data) {
      console.log(data);
      if(Object.keys(data).length > 0){
        dbmd = data;
        var tables = Object.values(dbmd);

        console.log(tables);
        if(!sort){
          sort = "3";
          console.log('Default to sort ' + sort);
        }

        switch(sort){
          case "0":
            tables.sort(function(a, b){
              return a.table_name.localeCompare(b.table_name);
            });
            break;
          case "1":
            tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
            break;
          case "2":
            tables.sort(function(a, b){return b.table_importedKeysCount - a.table_importedKeysCount});
            break;
          case "3":
            tables.sort(function(a, b){return b.table_importedKeysSeqCount - a.table_importedKeysSeqCount});
            break;
          case "4":
            tables.sort(function(a, b){return b.table_exportedKeysCount - a.table_exportedKeysCount});
            break;
          case "5":
            tables.sort(function(a, b){return b.table_exportedKeysSeqCount - a.table_exportedKeysSeqCount});
            break;
          case "6":
            tables.sort(function(a, b){return b.table_indexesCount - a.table_indexesCount});
            break;
          case "7":
            tables.sort(function(a, b){return b.table_recCount - a.table_recCount});
            break;
          default:
            tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
        }

        $.each(tables, function(i, obj){
          //console.log(obj.name);
          // var dataContent = "<span class='label label-success'>" + obj.RecCount + "</span>";
          var option = '<option class="fontsize" value="' + obj.table_name + '" data-subtext="' + obj.table_remarks +  ' ' + obj.table_stats + '">'
           + obj.table_name + '</option>';
          table.append(option);
          // $('#modPKTables').append(option);
          // table.append('<option class="fontsize" value=' + obj.name + '>' + obj.name + '</option>');
        });
        table.selectpicker('refresh');
        // $('#modPKTables').selectpicker('refresh');
        // localStorage.setItem('tables', JSON.stringify(tables));
      }
      else {
        console.log("GetTables");
        $.ajax({
        type: 'POST',
        url: "GetTables",
        dataType: 'json',
        async: true,
        success: function(tables) {
          $.each(tables, function(i, obj){
            var option = '<option class="fontsize" value="' + obj.name + '">' + obj.name + '</option>';
            table.append(option);
          });
          table.selectpicker('refresh');
        }
      });
    }
  }
  })
}

function GetCognosLocales(){
  $.ajax({
      type: 'POST',
      url: "GetCognosLocales",
      dataType: 'json',

      success: function(data) {
        cognosLocales = data.cognosLocales;
        console.log(cognosLocales);
      },
      error: function(data) {
          console.log(data);
          showalert("GetCognosLocales()", "GetCognosLocales failed.", "alert-danger", "bottom");
      }
  });
}

function ChooseField(table, id){
  table.empty();

  var datas = $datasTable.bootstrapTable('getData');
  $.each(datas, function(i, obj){
    if(obj._id == id){
      $.each(obj.fields, function(j, field){
        var icon = "";
        if(field.pk){
          icon = "<i class='glyphicon glyphicon-star'></i>";
        }
        if(field.index && !field.pk){
          icon = "<i class='glyphicon glyphicon-star-empty'></i>";
        }
        var label = field.label;
        var subText = icon;
        if(label){
          subText += ' - ' + label;
        }

        table.append('<option class="fontsize" value="' + field.field_name + '" data-subtext="' + subText + '">' + field.field_name + '</option>');
      });
      table.selectpicker('refresh');
    }
  });

  if( table.has('option').length == 0 ) {
    $.ajax({
        type: 'POST',
        url: "GetFields",
        dataType: 'json',
        data: "table=" + id,

        success: function(data) {
            $.each(data, function(index, detail){
              var icon = "";
              if(detail.pk){
                icon = "<i class='glyphicon glyphicon-star'></i>";
              }
              if(detail.index && !detail.pk){
                icon = "<i class='glyphicon glyphicon-star-empty'></i>";
              }

              var label = detail.label;
              var subText = icon;
              if(label){
                subText += ' - ' + label;
              }

              table.append('<option class="fontsize" value"' + detail.field_name + '" data-subtext="' + subText + '">' + detail.field_name + '</option>');
            });
            table.selectpicker('refresh');
            // showalert("ChooseField()", "ChooseField was successfull.", "alert-success", "bottom");
        },
        error: function(data) {
            console.log(data);
            showalert("ChooseField()", "ChooseField failed.", "alert-danger", "bottom");
        }

    });

  }

}

function showalert(title, message, alertType, area) {

    $('#alertmsg').remove();

    var timeout = 5000;

    if(area == undefined){
      area = "bottom";
    }
    if(alertType.match('warning')){
      area = "bottom";
      timeout = 10000;
    }
    if(alertType.match('danger')){
      area = "bottom";
      timeout = 30000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<h4>' + title + '</h4>' +
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType + ' flyover flyover-' + area);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<h4>' + title + '</h4>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible flyover flyover-' + area);
    }

    $('#Alert').append($newDiv);

    if ( !$('#alertmsg').is( '.in' ) ) {
      $('#alertmsg').addClass('in');

      setTimeout(function() {
         $('#alertmsg').removeClass('in');
      }, timeout);
    }
}

function TestDBConnection() {

    $.ajax({
        type: 'POST',
        url: "TestDBConnection",
        dataType: 'json',

        success: function(data) {
            console.log(data);
            showalert("TestDBConnection()", "Connection to database was successfull.", "alert-success", "bottom");
        },
        error: function(data) {
            console.log(data);
            showalert("TestDBConnection()", "Connection to database failed.", "alert-danger", "bottom");
        }

    });

}

function OpenSetProjectModal(){

 $projectFileModal.modal('toggle');
}

function SetProjectName(){
  var projectName = $projectFileModal.find('#filePath').val();
  console.log("projectName=" + projectName);
	if (!$.isNumeric(projectName)) {
	    showalert("SetProjectName()", "Enter a numeric value.", "alert-warning", "bottom");
	    return;
  	}
  $.ajax({
		type: 'POST',
		url: "SetProjectName",
		// dataType: 'json',
		data: "projectName=" + "model-" + projectName,

		success: function(data) {
			Publish();
		},
		error: function(data) {
			showalert("SetProjectName()", "Error when setting projectName.", "alert-danger", "bottom");
		}
	});

  $projectFileModal.modal('toggle');
}

function Publish(){

  var projectName = "";
  $datasTable.bootstrapTable("filterBy", {});
	var data = $datasTable.bootstrapTable('getData');

  if (data.length == 0) {
    showalert("Publish()", "Nothing to publish.", "alert-warning", "bottom");
    return;
  }

  bootbox.prompt({
    size: "small",
    title: "Enter project name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      projectName = result;
      if(!projectName){
        return;
      }

      var parms = {projectName: projectName, data: JSON.stringify(data)};
      console.log(parms);

      $.ajax({
    		type: 'POST',
    		url: "SendQuerySubjects",
    		dataType: 'json',
    		data: JSON.stringify(parms),

    		success: function(data) {
    			// $('#DatasTable').bootstrapTable('load', data);
          console.log(data);
          showalert("Publish()", data.message, "alert-success", "bottom");
    		},
    		error: function(data) {
    			showalert("Publish()", "Publish failed.", "alert-danger", "bottom");
    		}
    	});

      // ApplyFilter();

    }
  });

}

function SaveModel(){

  $datasTable.bootstrapTable("filterBy", {});
  var modelName;
	var data = $datasTable.bootstrapTable('getData');

  if (data.length == 0) {
    showalert("SaveModel()", "Nothing to save.", "alert-warning", "bottom");
    return;
  }

  $.each(data, function(i, qs){
    var labels = {};
    qs.labels = labels;
    var descriptions = {};
    qs.descriptions = descriptions;
    $.each(cognosLocales, function(j, locale){
      qs.labels[locale] = qs.label;
      qs.descriptions[locale] = qs.description;
    })
  })

  bootbox.prompt({
    size: "small",
    title: "Enter model name",
    callback: function(result){
     /* result = String containing user input if OK clicked or null if Cancel clicked */
    modelName = result;
    if(!modelName){
      return;
    }

    var parms = {modelName: modelName, data: data};

   	$.ajax({
   		type: 'POST',
   		url: "SaveModel",
   		dataType: 'json',
   		data: JSON.stringify(parms),

   		success: function(data) {
   			showalert("SaveModel()", "Model saved successfully.", "alert-success", "bottom");
   		},
   		error: function(data) {
   			showalert("SaveModel()", "Saving model failed.", "alert-danger", "bottom");
   		}
   	});

    // ApplyFilter();

    }
  });

}

function GetModelList(){

  $modelListModal.modal('toggle');

	$.ajax({
		type: 'POST',
		url: "GetModelList",
		dataType: 'json',

		success: function(data) {
      modelList = data;
      data.sort(function(a, b) {
        return b - a;
      });
      console.log("modelList");
      console.log(modelList);
			// showalert("GetModelList()", "Model list get successfull.", "alert-success", "bottom");

		},
		error: function(data) {
			showalert("GetModelList()", "Getting model list failed.", "alert-danger", "bottom");
		}
	});

}

function OpenModel(id){

  var modelName;

  $.each(modelList, function(i, obj){
    if(obj.id == id){
      modelName = obj.name;
    }
  });

  console.log("modelName=" + modelName);

	$.ajax({
		type: 'POST',
		url: "OpenModel",
		dataType: 'json',
    data: "model=" + modelName,

		success: function(data) {
      $datasTable.bootstrapTable("load", data);
      if(activeTab == "Final"){
        $datasTable.bootstrapTable("filterBy", {type: ['Final']});
      }
			// showalert("OpenModel()", "Model opened successfully.", "alert-success", "bottom");

		},
		error: function(data) {
			showalert("OpenModel()", "Opening model failed.", "alert-danger", "bottom");
		}
	});

  $modelListModal.modal('toggle');

}

function GetDBDataType() {

	$.ajax({
        type: 'POST',
        url: "GetDataType",
        dataType: 'json',

        success: function(data) {
			       dbDataType = data;
             dbDataType.push({value: "", text: ""});
        },
        error: function(data) {
            console.log(data);
        }

    });
}


function Logout(){

  $('#modLogout').modal('toggle');

  return;

  bootbox.confirm({
    message: "Do you really want to logout ?",
    buttons: {
        confirm: {
            label: 'Yes',
            className: 'btn-danger'
        },
        cancel: {
            label: 'No',
            className: 'btn-success'
        }
    },
    callback: function (result) {

      if(!result){
        return;
      }

      // $.ajax({
      //       type: 'POST',
      //       url: "ibm_security_logout",
      //       data: 'logout=Logout&logoutExitPage=%2Flogin.html',
      //
      //       success: function(data) {
      //         console.log(data);
      //       },
      //       error: function(data) {
      //         console.log(data);
      //       }
      //
      //   });

        var XHR = new XMLHttpRequest();
          var FD  = new FormData();

          // Push our data into our FormData object
          FD.append("logout", "Logout");
          // FD.append("logoutExitPage", "login.html");

          // Define what happens on successful data submission
          XHR.addEventListener('load', function(event) {
            console.log('Yeah! Data sent and response loaded.');
          });

          // Define what happens in case of error
          XHR.addEventListener('error', function(event) {
            console.log('Oops! Something went wrong.');
          });

          // Set up our request
          XHR.open('POST', 'Logout');

          XHR.setRequestHeader("Content-type","application/x-www-form-urlencoded");

          // Send our FormData object; HTTP headers are set automatically
          XHR.send(FD);

    }

});
}

function Reset() {

	var success = "OK";

	$.ajax({
        type: 'POST',
        url: "Reset",
        dataType: 'json',

        success: function(data) {
			success = "OK";
        },
        error: function(data) {
            console.log(data);
   			success = "KO";
        }

    });

	if (success == "KO") {
		showalert("Reset()", "Operation failed.", "alert-danger", "bottom");
	}

  // window.location = window.location.href+'?eraseCache=true';
  // localStorage.setItem('dbmd', null);
	location.reload(true);

}

function GetTableData(){
		var data = $datasTable.bootstrapTable("getData");
		console.log("data=");
		console.log(JSON.stringify(data));
    console.log(data);

}

function RemoveAll(){
  $datasTable.bootstrapTable("removeAll");
}

function ExpandAll(){
  $datasTable.bootstrapTable('expandAllRows');
}

function GetDBMDFromCache(){

    $.when(
      $.ajax({
        type: 'POST',
        url: "GetDBMDFromCache",
        dataType: 'json',
        async: true,
        success: function(data) {
          // dbmd = data;
        }
      })
    )
    .then(
      function(data){
        dbmd = data;
        console.log("Got dbmd");
        // localStorage.setItem('dbmd', JSON.stringify(dbmd));
        console.log(dbmd);
        ChooseTable($tableList);
      }
    );

}
