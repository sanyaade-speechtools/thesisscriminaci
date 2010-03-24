var jsonrpc = null;

window.onload = function () {
	jsonrpc = new JSONRpcClient("/programd/JSON-RPC"); 
	
	if($('#manager_page').length>0){
		ManagerPage.init();
	}
}

var ManagerPage = {
	
	content_div: '.content',
	active_link_class: 'activelink',
	menu_link_path: '.navigation > ul > li > a',
	menu_link_path_active: '.navigation > ul > li > .activelink',
	
	selected: null,
	
	
	home_template_div: '#home_template',
	home_menu_id: 'home_link',
	plugins_template_div: '#plugins_template',
	plugins_menu_id: 'plugins_link',
	plugins_menu_selector: '.selector',
	plugins_menu_templ: '#plugins_selector_template',
	loading_template_div: '#loading_template',
	loading_menu_id: 'loaging_link',
	lsa_manager_menu_id: 'lsa_manager_link',
	lsa_manager_menu_selector: '.selector',
	lsa_manager_menu_templ: '#lsa_manager_selector_template',
	lsa_manager_template_div: '#lsa_manager_template',
	lsa_manager_lsa_plugin_off: 'Per utilizzare questa sezione devi attivare il plugin lsa!',
	
	selector_item_name: '.selector_item_name',
	selector_item_property: '.selector_item_property',
	btn_action: '.btn_action',
	
	init: function(){
		ManagerPage.goHome();	
		
		var links = $(ManagerPage.menu_link_path);
		for(var i=0; i<links.length; i++){
			var link = links[i];
			var id = $(link).attr('id');
			$(link).attr('href','javascript:ManagerPage.changeMenu(\''+id+'\')');
		}
		
	},
	
	changeMenu: function(linkId){
		
		ManagerPage.changeMenuOver(linkId);
		
		$(ManagerPage.content_div).empty();
		
		if(linkId==ManagerPage.home_menu_id){
			ManagerPage.goHome();
		}else if(linkId==ManagerPage.plugins_menu_id){
			ManagerPage.goPlugins();
		}else if(linkId==ManagerPage.loading_menu_id){
			ManagerPage.goLoading();
		}else if(linkId==ManagerPage.lsa_manager_menu_id){
			ManagerPage.goLsaManager();
		}
		
	},
	
	goHome: function(){
		var home = $(ManagerPage.home_template_div).clone();
		
		ManagerPage.replateContent(home);
	},	
	
	goPlugins: function(){
		jsonrpc.coreServices.getPlugins(ManagerPage.goPluginsCallback);		
	},
	
	goLoading: function(){
		var loading = $(ManagerPage.loading_template_div).clone();
		
		ManagerPage.replateContent(loading);
	},
	
	goLsaManager: function(){
		if(jsonrpc.lsaServices!=null){
			jsonrpc.lsaServices.getAllLsaSpaces(ManagerPage.goLsaManagerCallback);
		}
		else{
			alert(ManagerPage.lsa_manager_lsa_plugin_off);
			ManagerPage.changeMenu(ManagerPage.plugins_menu_id);
		}
	},
	
	goPluginsCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null){
			return;
		}
		
		var plugins = $(ManagerPage.plugins_template_div).clone();
		
		var container = $(plugins).find(ManagerPage.plugins_menu_selector);
		var template = $(plugins).find(ManagerPage.plugins_menu_templ);
		
		for(var i=0; i<result.list.length; i++){
			var pl = result.list[i];
			var el = $(template).clone();
			
			$(el).find(ManagerPage.selector_item_name).text(pl.name);
			
			if(pl.active){
				$(el).find(ManagerPage.selector_item_property+' > span').text('active');
				$(el).find(ManagerPage.selector_item_property+' > span').val(pl.name);
				
				$(el).find('.item_inactive').attr('OnClick','ManagerPage.deactivePlugin(\''+pl.name+'\')');
				$(el).find('.item_inactive').show();
			}else{
				$(el).find(ManagerPage.selector_item_property+' > span').text('inactive');
				$(el).find(ManagerPage.selector_item_property+' > span').val(pl.name);				
				$(el).find('.item_active').attr('OnClick','ManagerPage.activePlugin(\''+pl.name+'\')');
				
				$(el).find('.item_active').show();
			}
			
			$(el).attr('id','plugin_'+pl.name);
			$(el).show();
			$(container).append(el);			
		}
		
		$(plugins).find(ManagerPage.btn_action).attr('OnClick','ManagerPage.reloadCore()');
		
		ManagerPage.replateContent(plugins);
	},
	
	goLsaManagerCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null){
			return;
		}
		
		var lsaManager = $(ManagerPage.lsa_manager_template_div).clone();
		var container = $(lsaManager).find(ManagerPage.lsa_manager_menu_selector);
		var template = $(lsaManager).find(ManagerPage.lsa_manager_menu_templ);
		
		for(var i=0; i<result.list.length; i++){
			var lsa = result.list[i];
			var el = $(template).clone();
			
			$(el).find(ManagerPage.selector_item_name).text(lsa.name);
			
			$(el).find(ManagerPage.selector_item_property+':eq(0) > span').text(lsa.dbUrl);
			$(el).find(ManagerPage.selector_item_property+':eq(0) > span').val(lsa.name);
			
			$(el).find(ManagerPage.selector_item_property+':eq(1) > span').text(lsa.type);
			$(el).find(ManagerPage.selector_item_property+':eq(1) > span').val(lsa.name);
			
			$(el).find(ManagerPage.selector_item_property+':eq(2) > span').text(lsa.nsigma);
			$(el).find(ManagerPage.selector_item_property+':eq(2) > span').val(lsa.name);
									
			$(el).find('.item_delete').attr('OnClick','ManagerPage.deleteLsaSpace(\''+lsa.id+'\')');			
			$(el).find('.item_delete').show();
			
			$(el).find('.item_edit').attr('OnClick','ManagerPage.editLsaSpace(\''+lsa.name+'\', \''+lsa.dbUrl+'\', \''+lsa.type+'\', \''+lsa.nsigma+'\')');			
			$(el).find('.item_edit').show();
						
			if(lsa.status=='Online'){
				$(el).find('.item_process').attr('OnClick','ManagerPage.processLsaSpace(\''+lsa.id+'\', \'process_'+lsa.id+'\')');		
				$(el).find('.item_process').attr('id','process_'+lsa.id);
				$(el).find('.item_process').show();
			}else if(lsa.status=='Processing'){	
				$(el).find('.item_loading').attr('id','process_'+lsa.id);
				$(el).find('.item_loading').show();	
			}
										
			$(el).attr('id','lsa_space_'+lsa.name);
			$(el).show();
			$(container).append(el);
		}	

		$(lsaManager).find(ManagerPage.btn_action+':eq(1)').attr('OnClick','ManagerPage.openFileManager()');		
		$(lsaManager).find(ManagerPage.btn_action+':eq(2)').attr('OnClick','ManagerPage.addLsaSpace()');
		
		ManagerPage.replateContent(lsaManager);
	},
	
	processLsaSpace: function(id, el_id){
		$('#'+el_id).hide();
		$('#'+el_id).parent().find('.item_loading').show();		
		
		jsonrpc.lsaServices.processLsaSpace(ManagerPage.processLsaSpaceCallback, id);
	},
	
	processLsaSpaceCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null){
			return;
		}
		
		var id = 'process_'+result.id;
		
		$('#'+id).parent().find('.item_loading').hide();
		$('#'+id).show();
	},
	
	editLsaSpace: function(name, dbUrl, type, nsigma){
		var lsaManager = $('.content #lsa_manager_template');
		
		$(lsaManager).find('.selector_add_item').fadeOut('slow');
		$(lsaManager).find('.plugin_legend').fadeOut('slow');
		$(lsaManager).find(ManagerPage.btn_action+':eq(1)').fadeOut('slow');
		$(lsaManager).find(ManagerPage.btn_action+':eq(2)').fadeOut('slow', ManagerPage.editLsaSpaceShow);
		$('.content .selector_edit_item').find('.selector_item_name > span').text(name);
		$('.content .selector_edit_item').find('.selector_item_property:eq(0) > span').text(dbUrl);
		$('.content .selector_edit_item').find('.selector_item_property:eq(1) > span').text(type);
		$('.content .selector_edit_item').find('.lsa_input_nsigma').val(nsigma);
					
		$(lsaManager).find(ManagerPage.btn_action+':eq(0)').attr('OnClick','ManagerPage.editLsaSpaceAction(\''+name+'\', \''+type+'\', \''+nsigma+'\')');
	},
	
	editLsaSpaceShow: function(){
		$('.content .selector_edit_item').fadeIn('slow');	
	},
	
	editLsaSpaceHide: function(){				
		var lsaManager = $('.content #lsa_manager_template');
		$(lsaManager).find('.selector_add_item').fadeIn('slow');
		$(lsaManager).find('.plugin_legend').fadeIn('slow');
		$(lsaManager).find(ManagerPage.btn_action+':eq(1)').fadeIn('slow');
		$(lsaManager).find(ManagerPage.btn_action+':eq(2)').fadeIn('slow');

		$(lsaManager).find(ManagerPage.btn_action+':eq(0)').attr('OnClick','');
		
		ManagerPage.changeMenu(ManagerPage.lsa_manager_menu_id);
	},
	
	editLsaSpaceAction: function(name, type, nsigma){		
		nsigma = $('.content .selector_edit_item').find('.lsa_input_nsigma').val();	
		jsonrpc.lsaServices.editLsaSpace(ManagerPage.editLsaSpaceCallback, name, type, nsigma);
	},
	
	editLsaSpaceCallback: function(result, e){
	
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null && result==false){
			return;
		}
		
		$('.content .selector_edit_item').fadeOut('slow', ManagerPage.editLsaSpaceHide);
	},
	
	deleteLsaSpace: function(id){
		jsonrpc.lsaServices.removeLsaSpace(ManagerPage.deleteLsaSpaceCallback, id);
	},
	
	deleteLsaSpaceCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null || result==false){
			return;
		}
		
		ManagerPage.changeMenu(ManagerPage.lsa_manager_menu_id);
	},
	
	addLsaSpace: function(){
		var name = jQuery.trim($('.content .selector_add_item .lsa_input_name').val());
		var nsigma = jQuery.trim($('.content .selector_add_item .lsa_input_nsigma').val());
		if(name.length > 0){
			var type = $('.content .lsa_select_type').val();
			
			jsonrpc.lsaServices.addLsaSpace(ManagerPage.addLsaSpaceCallback,name, type, nsigma);
		}
		
	},

	addLsaSpaceCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result==null){
			return;
		}
		
		ManagerPage.changeMenu(ManagerPage.lsa_manager_menu_id);	
	},	
	
	reloadCore: function(){
		ManagerPage.changeMenu(ManagerPage.loading_menu_id);
		jsonrpc.coreServices.reloadCore(ManagerPage.reloadCoreCallback);
	},
	
	reloadCoreCallback: function(result, e){
		if(e!=null){
			return;
		}
		if(result==null || result==false){
			return;
		}
		jsonrpc = new JSONRpcClient("/programd/JSON-RPC");
		ManagerPage.changeMenu(ManagerPage.plugins_menu_id);
	},
	
	activePlugin: function(plugin){
		jsonrpc.coreServices.activePlugin(ManagerPage.activePluginCallback,plugin);
	},		
	
	activePluginCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result!=true){
			return;
		}
		ManagerPage.changeMenu(ManagerPage.plugins_menu_id);
	},
	
	deactivePlugin: function(plugin){
		jsonrpc.coreServices.deactivePlugin(ManagerPage.deactivePluginCallback,plugin);
	},		
	
	deactivePluginCallback: function(result, e){
		if(e!=null){
			alert(e);
			return;
		}
		if(result!=true){
			return;
		}
		ManagerPage.changeMenu(ManagerPage.plugins_menu_id);
	},
	
	replateContent: function(element){		
		$(element).show();
		$(ManagerPage.content_div).append(element);
	},
	
	changeMenuOver: function(linkId){
		if($(ManagerPage.menu_link_path_active).hasClass(ManagerPage.active_link_class)){
			$(ManagerPage.menu_link_path_active).removeClass(ManagerPage.active_link_class);
		}
		
		ManagerPage.selected=$('#'+linkId);
		if(!$('#'+linkId).hasClass(ManagerPage.active_link_class)){
			$('#'+linkId).addClass(ManagerPage.active_link_class);
		}
	},
	
	openFileManager: function(type,field,imgId){
		var url = "../filemanager/browser.html?";
		var width = 800;
		var height = 600;
		var left = (screen.width - width)/2;
		var top = (screen.height - height)/2;
		var params = 'width='+width+', height='+height;
		params += ', top='+top+', left='+left;
		params += ', directories=no';
		params += ', location=no';
		params += ', menubar=no';
		params += ', resizable=no';
		params += ', scrollbars=no';
		params += ', status=no';
		params += ', toolbar=no';
		if(type)
			url += "&Type=" + type;
		if(field)
			url += "&OpenFile=" + field;
		if(imgId)
			url += "&ImageId=" + imgId;

		url += "&RelatedPath=true";
		windowPopup=window.open(url,name, params);
		if (windowPopup.focus){
 			windowPopup.focus();
 		}
	} 
}