function fixDialogPosition() {	
	var $dlg = $(".doPositionDialog.ui-overlay-visible");
	var left = ($(window).width()-$dlg.width())/2;
	var top = ($(window).height()-$dlg.height())/2;
	
	$dlg.css('position', 'fixed').css('left', left+"px").css('top', top+"px");
}

function fixProposedItems() {
	//console.log("fixProposedItems()");
//	$(".ui-selectmanybutton.selectManyProperties > .ui-button.ui-state-disabled, .ui-selectonebutton.selectOneProperty > .ui-button.ui-state-disabled")
//		.addClass('proposedItem')
//		.removeClass('ui-state-disabled')
//		.hover(function() {
//			$(this).addClass("ui-state-hover");
//		}, function() {
//			$(this).removeClass("ui-state-hover");
//		})
//		.click(function(event) {
//			event.preventDefault();
//			$(this).toggleClass("ui-state-active");
//		});
	
	$(".ui-selectmanybutton.selectManyProperties > .ui-button:not(.ui-disabled), .ui-selectonebutton.selectOneProperty > .ui-button:not(.ui-disabled)").each(function(index)
	{
		if($(this).attr('title')=="true")
		{
			$(this).addClass('proposedItem')
				//.removeClass('ui-state-disabled')
				.hover(function() {
					$(this).addClass("ui-state-hover");
				}, function() {
					$(this).removeClass("ui-state-hover");
				})
				.click(function(event) {
					event.preventDefault();
					$(this).toggleClass("ui-state-active");
				});
		}
	});
	
	$('table.proposedMeasures td>span').each(function(index)
	{
		if($(this).attr('title')=="true")
		{
			$(this).addClass('proposedLegend');
		}
	});
}
function selectChanged() {
	setTimeout(function(){
		var $activeAccordionHeader = $('.ui-accordion-header.ui-state-active');
		var $activeAccordionContent = $activeAccordionHeader.next('.ui-accordion-content');
		var proposalsSelect = $activeAccordionContent.find('select')[1];
		var typeSelect = $activeAccordionContent.find('.ui-selectonemenu')[0];
		if($(proposalsSelect).val()!="@@@@") {
			$activeAccordionContent.find('input').attr('disabled', 'true').removeClass('ui-state-default').addClass('ui-state-disabled');
			$(typeSelect).removeClass('ui-state-default').addClass('ui-state-disabled');
		}
		else {
			$activeAccordionContent.find('input').attr('disabled', 'false').removeClass('ui-state-disabled').addClass('ui-state-default');
			$(typeSelect).removeClass('ui-state-disabled').addClass('ui-state-default');
		}
	}, 500);
	
}
$(function(){	
	$(document).on('input propertychange', '.restrictedInput',function() {
		$(this).val($(this).val().replace(/[^a-z 0-9]/i, ""));
    });
	$(document).on('input propertychange', '.restrictedInputNoNumbers',function() {
		$(this).val($(this).val().replace(/[^a-z ]/i, ""));
    });
	
	fixProposedItems();
});