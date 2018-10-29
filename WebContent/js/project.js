var datas = {};

$(document)
.ready(function() {
  GetUserInfos();
  $('#dynamicModal').modal('toggle');
})

// $('.modal').modal({
//     backdrop: 'static',
//     keyboard: false
// })

$('.modal').on('shown.bs.modal', function() {
  $(this).find('[autofocus]').focus();
});

$('#newProjectModal').on('shown.bs.modal', function(){
  GetResources()
});

$('button#create').click(function(){
  if($("#prjResource").val() == "" | $("#prjName").val() == ""){
    ShowAlert("Neither Name nor Resource should be left empty.", "alert-warning");
    return;
  }
  NewProject();

});

$(".list-group a").click(function() {

  var $id = $(this).attr("id");
  console.log("$id=" + $id);

  if($id == "open"){
    $modal = $('#dynamicModal');
    $modal.find('.modal-header').find('.modal-title').empty();
    $modal.find('.modal-body').empty();
    $modal.find('.modal-footer').empty();

    var openTitle = "<h4>Existing project(s).</h4>"

    var openBody = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
    openBody += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." autofocus/></div>';
    openBody += '<div id="searchlist" class="list-group">';

    $.each(datas.PROJECTS, function(i, obj){
      openBody += '<a href="#" id="' + i +'" class="list-group-item"><span>' + obj.name + ' - ' + obj.timestamp + '<br>' +
        obj.resource.dbName + ' - ' + obj.dbSchema + ' - ' + obj.resource.dbEngine +
        '<br>' + obj.resource.cognosCatalog + ' - ' + obj.resource.cognosDataSource + ' - ' + obj.resource.cognosSchema +
        '<br>' + obj.description +
        '</span></a>';
    });
    openBody += '</div></form></div></div><script>';
    openBody += '$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false,});';
    openBody += '$(".list-group a").click(function(){OpenProject($(this).attr("id"));});';
    openBody += '</script>';

    var footer = '<input type="button" class="btn btn-default" id="back" value="Back">';
    footer += '<script>$("#back").click(function(){location.reload(true);});</script>';

    $modal.find('.modal-header').find('.modal-title').append(openTitle);
    $modal.find('.modal-body').append(openBody);
    $modal.find('.modal-footer').append(footer);
  }
  else{
    $('#newProjectModal').modal('toggle');
  }

});

$("#prjResource").change(function () {
    var selectedText = $(this).find("option:selected").val();
    var dbSchema = datas.RESOURCES[selectedText].jndiName.split('.')[1];
    console.log(selectedText);
    console.log(dbSchema);
		$('#prjDbSchema').val(dbSchema);
});

function GetUserInfos() {
  $.ajax({
    type: 'POST',
    url: "GetUserInfos",
    dataType: 'json',
    success: function(data) {
      $('#dynamicModalLabel').text('Welcome ' + data.USER);
      if(!data.PROJECTS){
        $("a#open").addClass('disabled');
      }
      console.log(data);
      datas = data;
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function GetResources() {
  $.ajax({
    type: 'POST',
    url: "GetResources",
    dataType: 'json',
    success: function(data) {
      console.log(data);
      // var resource = {};
      // resource = data;
      datas.RESOURCES = data;
      // console.log("datas" + JSON.stringify(datas));
      console.log(datas);
      loadList($("#prjResource"), datas.RESOURCES);
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function NewProject() {

  var prj = {};
  prj.name = $("#prjName").val();
  prj.dbSchema = $("#prjDbSchema").val();
  prj.description = $("#prjDescription").val();
  prj.resource = datas.RESOURCES[$("#prjResource").val()];

  $.ajax({
    type: 'POST',
    url: "NewProject",
    dataType: 'json',
    data: JSON.stringify(prj),
    success: function(data) {
      console.log(data);
      if(data.STATUS == "KO"){
        ShowAlert(data.REASON, 'alert-danger');
      }
      if(data.STATUS == "OK"){
        window.location.replace("index.html");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function OpenProject(id) {

  var prj = datas.PROJECTS[id];

  $.ajax({
    type: 'POST',
    url: "OpenProject",
    dataType: 'json',
    data: JSON.stringify(prj),
    success: function(data) {
      console.log(data);
      if(data.STATUS == "KO"){
        ShowAlert(data.REASON, 'alert-danger');
      }
      if(data.STATUS == "OK"){
        window.location.replace("index.html");
      }
    },
    error: function(data) {
      console.log(data);
    }
  });
}

function loadList(obj, list){
  obj.empty();
  $.each(list, function(i, item){
    var option = '<option class="fontsize" value="' + i + '" data-subtext="' + item.dbName + ' - ' + item.dbEngine
      + ' - ' + item.cognosCatalog + ' - ' + item.cognosDataSource + ' - ' + item.cognosSchema
      + '">' + item.jndiName + '</option>';
    obj.append(option);
  });
  obj.selectpicker('refresh');

}

function ShowAlert(message, alertType) {

    $('#alertmsg').remove();

    var timeout = 3000;

    if(alertType.match('alert-warning')){
      timeout = 5000;
    }
    if(alertType.match('alert-danger')){
      timeout = 15000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible');
    }

    $('#Alert').append($newDiv);

    setTimeout(function() {
       $('#alertmsg').remove();
    }, timeout);

}
