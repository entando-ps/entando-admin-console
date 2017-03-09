$(function () {

	var serviceUrl = PROPERTY.baseUrl + 'do/rs/PageModel/frames?code=' + PROPERTY.pagemodel,
		addWidgetUrl = PROPERTY.baseUrl + 'do/rs/Page/joinWidget?code=' + PROPERTY.pagemodel,
		moveWidgetUrl = PROPERTY.baseUrl + 'do/rs/Page/moveWidget?code=' + PROPERTY.pagemodel,
		deleteWidgetUrl = PROPERTY.baseUrl + 'do/rs/Page/deleteWidget?code=' + PROPERTY.pagemodel,
		getPageDetailUrl = PROPERTY.baseUrl + 'do/rs/Page/detail',
		restoreOnlineUrl = PROPERTY.baseUrl + 'do/rs/Page/restoreOnlineConfig',
		setOnlineUrl = PROPERTY.baseUrl + 'do/rs/Page/setOnline',
		setOfflineUrl = PROPERTY.baseUrl + 'do/rs/Page/setOffline',
		configureWidgetUrl = PROPERTY.baseUrl + 'do/Page/editFrame.action',
		apiMappingsUrl = PROPERTY.baseUrl + 'do/rs/Portal/WidgetType/apiMappings',
		apiCopyFromWidgetUrl = PROPERTY.baseUrl + 'do/Api/Service/copyFromWidget.action',
		PAGE_IS_SELECTED = !!PROPERTY.pagemodel;



	var gridSlots = {}, // contains previous slots HTML
		apiMappings = {}, // contains the API mappings
		pageData = null,  // contains page details data
		pageFrames = [],  // contains the page frames
		alertService = new EntandoAlert('.alert-container');


	// jQuery selectors
	var $pageCircle = $('#pageTree tr#' + PROPERTY.code + ' .statusField .fa'),
		$restoreOnlineBtn = $('.restore-online-btn'),
		$publishBtn = $('.publish-btn'),
		$unpublishBtn = $('.unpublish-btn'),
		$pageInfo = $('#page-info'),
		$pageTitleBig = $('.page-title-big'),
		$pageTitleTree = $('#pageTree tr#' + PROPERTY.code + ' .tree-item-page-title');

	/**
	 * Restores online configuration of the page
	 */
	function restoreOnlineConfig() {
		$.ajax(restoreOnlineUrl, {
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				pageCode: PROPERTY.code
			}),
			success: function (data) {
				if (alertService.showResponseAlerts(data)) {
					return;
				}
				pageData = data.page;
				initPage();
				updatePageStatus(pageData);
			}
		});
	}

	/**
	 * Publish / unpublish the page
	 * @param {boolean} online - true to publish, false to unpublish
	 */
	function setPageOnlineStatus(online) {
		$.ajax(online ? setOnlineUrl : setOfflineUrl, {
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				pageCode: PROPERTY.code
			}),
			success: function (data) {
				if (alertService.showResponseAlerts(data)) {
					return;
				}
				pageData = data.page;
				updatePageStatus(pageData);
			}
		});
	}




	/**
	 * Updates the page status circle color
	 */
	function updatePageStatus(pageData) {

		var hasChanges = !_.isEqual(pageData.draftWidgets, pageData.onlineWidgets) || !_.isEqual(pageData.draftMetadata, pageData.onlineMetadata);

		// updates the yellow/green page circle in the tree
		$pageCircle
			.removeClass('green yellow red')
			.addClass(pageData.online ? hasChanges ? 'yellow' : 'green' : 'red');

		// updates the buttons visibility
		var enablePublish = !(!pageData.online || pageData.online && hasChanges);
		var enableUnpublish = !pageData.online;
		var enableRestoreOnline = !(pageData.online && hasChanges);

		$restoreOnlineBtn.prop('disabled', enableRestoreOnline);
		$publishBtn.prop('disabled', enablePublish);
		$unpublishBtn.prop('disabled', enableUnpublish);



		// diff
		$('.diff-slot').removeClass('diff-slot');
		if (pageData.online) {
			if (pageData.draftMetadata.model.code !== pageData.draftMetadata.model.code) {
				$('.grid-slot').addClass('diff-slot');
			} else if (pageData.draftWidgets.length === pageData.onlineWidgets.length) {
				for (var i = 0; i < pageData.draftWidgets.length; ++i) {
					if (!_.isEqual(pageData.draftWidgets[i], pageData.onlineWidgets[i])) {
						$('.grid-slot[data-pos="' + i + '"]').addClass('diff-slot');
					}
				}
			}
		}

		// titles
		$pageTitleBig.text(pageData.draftTitles.en); // FIXME select based on curr language
		$pageTitleTree.text(pageData.draftTitles.en);

	}

	/**
	 * Initializes page detail and widget data
	 * @param {Object} pageData the current pageData object
	 */
	function updatePageDetail(pageData) {

		var metadata = pageData.draftMetadata,
			checkElems = {
				'true': '<span title="Yes" class="icon fa fa-check-square-o"></span>',
				'false': '<span title="No" class="icon fa fa-square-o"></span>'
			};
		$pageInfo.find('[data-info-pagecode]').text(pageData.code);
		var titles = _.map(metadata.titles, function (title, abbr) {
			return '<span class="monospace">(<abbr title="English">' + abbr + '</abbr>)</span> ' + title
		}).join(', ');
		$pageInfo.find('[data-info-titles]').html(titles);
		$pageInfo.find('[data-info-group]').text(pageData.group);
		$pageInfo.find('[data-info-model]').text(metadata.model.descr);
		$pageInfo.find('[data-info-showmenu]').html(checkElems[_.toString(pageData.showable)]);
		$pageInfo.find('[data-info-extratitles]').html(checkElems[_.toString(pageData.useExtraTitles)]);

	}



	function isEmptySlot(slot) {
		return _.isEmpty($(slot).find('.grid-widget'));
	}


	/**
	 * Shows a warning instead of a grid
	 * @param {string} alertText
	 */
	function showGridWarning(alertText) {
		var alert = '<div class="alert alert-warning">' +
			'<span class="pficon pficon-warning-triangle-o"></span>' +
			'<strong>' + alertText + '</strong>' +
			'</div>';
		$('.grid-container').html(alert);
	}

	/**
	 * Shows a warning instead of a grid
	 * @param {string} alertText
	 */
	function getMessageText(key, args) {
		var msg = TEXT[key] || '';
		if (_.isArray(args)) {
			for (var i = 0; i < args.length; ++i) {
				msg = msg.replace('{' + i + '}', args[i]);
			}
		}
		return msg;
	}


	function findWidgetInfo(widgetCode) {
		if (!pageData) {
			return null;
		}
		return _.find(pageData.draftWidgets, {type: {code: widgetCode}});
	}

	/**
	 * Creates a grid widget element
	 * @param {string} widgetCode
	 */
	function createGridWidget(widgetCode) {

		var $widget = $('.widget-square[data-widget-id="' + widgetCode + '"]').first(),
			widgetDescr = $widget.find('.widget-name').text(),
			$widgetIcon = $widget.find('.widget-icon').clone(),
			widgetInfo = findWidgetInfo(widgetCode);

		var html = '<div>' +
			'<div class="slot-name"></div>' +
			'</div>';


		function createMenuItem(label) {
			var $menuItem = $('<li role="presentation"><a role="menuitem" tabindex="-1" href="#"></a></li>');
			$menuItem.find('a[role="menuitem"]').text(label);
			return $menuItem;
		}

		var $dropdown = $('<div class="dropdown" />');
		$dropdown.append('<i class="menu-btn fa fa-ellipsis-v dropdown-toggle" type="button"  data-toggle="dropdown"></i>');

		var $dropDownMenu = $('<ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="dropdownMenu">');
		$dropdown.append($dropDownMenu);

		// create menu items
		var $detailsItem = createMenuItem(TEXT['widgetActions.details']);
		$dropDownMenu.append($detailsItem);
		$detailsItem.click(function (e) {
			window.location = PROPERTY.baseUrl +
				'do/Portal/WidgetType/viewWidgetUtilizers.action?widgetTypeCode=' + widgetCode
		});


		if (widgetInfo && widgetInfo.config) {

			var $settingsItem = createMenuItem(TEXT['widgetActions.settings']);
			$dropDownMenu.append($settingsItem);
			$settingsItem.click(function (e) {
				var framePos = +$(e.target).closest('.grid-slot').attr('data-pos');
				window.location = configureWidgetUrl +
					'?pageCode=' + PROPERTY.code + '&frame=' + framePos;
			});
		}

		var apiWidgetCode = widgetInfo && widgetInfo.type.logic ? _.get(widgetInfo, 'type.parentType.code', '') : widgetCode;
		if (PERMISSION.superuser && apiMappings[apiWidgetCode]) {
			var $apiItem = createMenuItem(TEXT['widgetActions.api']);

			$dropDownMenu.append($apiItem);
			$apiItem.click(function (e) {
				var framePos = +$elem.parent().attr('data-pos');
				window.location = apiCopyFromWidgetUrl +
					'?pageCode=' + PROPERTY.code +
					'&framePos=' + framePos +
					'&resourceName=' + apiMappings[apiWidgetCode].resourceName +
					'&namespace=' + apiMappings[apiWidgetCode].namespace;
			});
		}


		if (widgetInfo && widgetInfo.config && widgetInfo.type.logic === false) {
			var $newWidgetItem = createMenuItem(TEXT['widgetActions.newWidget']);
			$dropDownMenu.append($newWidgetItem);
			$newWidgetItem.click(function (e) {
				var framePos = +$elem.parent().attr('data-pos');
				window.location = PROPERTY.baseUrl +
					'do/Portal/WidgetType/copy.action?pageCode=' + PROPERTY.code + '&framePos=' + framePos
			});
		}


		var $deleteItem = createMenuItem(TEXT['widgetActions.delete']);
		$dropDownMenu.append($deleteItem);
		$deleteItem.click(function (e) {

			var framePos = +$elem.parent().attr('data-pos');

			// FIXME use styled modal
			if (!confirm('Delete widget "' + widgetCode + '" from page "' + PROPERTY.code + '" position "' + framePos + '"?')) {
				return;
			}

			// delete the widget
			$.ajax(deleteWidgetUrl, {
				method: 'POST',
				contentType: 'application/json',
				data: JSON.stringify({
					pageCode: PROPERTY.code,
					frame: framePos
				}),
				success: function (data) {
					if (alertService.showResponseAlerts(data)) {
						return;
					}
					setEmptySlot($elem.parent());

					// update local draft status
					pageData.draftWidgets[framePos] = null;
					updatePageStatus(pageData);
				}
			});
		});


		// widget element
		var $elem = $(html)
			.addClass('grid-widget instance')
			.attr('data-widget-id', widgetCode)
			.append($widgetIcon)
			.append('<div class="widget-name">' + widgetDescr + '</div>')
			.append($dropdown);


		return $elem;
	}


	/**
	 * Sets the slot name in the widget
	 */
	function setSlotName($widget, html) {
		$widget.find('.slot-name').html(html);
	}


	/**
	 * Populates a slot with the provided widget
	 * @param {jQuery} $slot
	 * @param {jQuery} $widget
	 */
	function populateSlot($slot, $widget) {
		setDraggable($widget, $slot);
		setSlotName($widget, _.unescape($slot.attr('data-description')));
		$slot.html($widget);
	}

	/**
	 * Empties a slot
	 * @param {jQuery} $slot
	 */
	function setEmptySlot($slot) {
		var key = $slot.attr('data-pos');
		$slot.html(gridSlots[key]);
	}

	/**
	 * Creates the grid given the frames data
	 */
	function updateGridPreview(data) {

		try {
			var gen = new GridGenerator({
				frames: data,
				rowHeight: 80
			});

			var gridHtml = '<div class="grid-preview">' + gen.getHtml() + '</div>';
			$('.grid-container').html(gridHtml);

			// init original html map (empty slot)
			$('.grid-slot').each(function (index, el) {
				var pos = $(el).attr('data-pos');
				gridSlots[pos] = $(el).html();
			});

			// populates the slots
			_.forEach(pageData.draftWidgets, function (widget, index) {
				if (widget) {
					var $curWidget = createGridWidget(_.get(widget, 'type.code'));
					var $slot = $('.grid-slot[data-pos="' + index + '"]');
					populateSlot($slot, $curWidget);
				}
			});

			$('.grid-slot').droppable({
				accept: function (draggable) {

					var isFree = isEmptySlot(this) || !isEmptySlot($(draggable).parent()),
						isWidget = $(draggable).hasClass('widget-square')
							|| $(draggable).hasClass('grid-widget');
					return isWidget && isFree;
				},
				drop: function (ev, ui) {

					var $prevSlot = $(ui.draggable).parent(),
						$curSlot = $(ev.target),
						$curWidget = $(ui.draggable),
						curWidgetType = $curWidget.attr('data-widget-id'),
						curSlotPos = +$curSlot.attr('data-pos');


					if ($prevSlot.is($curSlot)) {
						return;
					} else {
						// replaces the grid slot html with the old (empty) one
						var html = gridSlots[+$prevSlot.attr('data-pos')];
						$prevSlot.append(html);
					}


					// it's a widget square
					if (!$curWidget.hasClass('instance')) {
						$curWidget = createGridWidget(curWidgetType);

						// add the widget
						$.ajax(addWidgetUrl, {
							method: 'POST',
							contentType: 'application/json',
							data: JSON.stringify({
								pageCode: PROPERTY.code,
								widgetTypeCode: curWidgetType,
								frame: curSlotPos
							}),
							success: function (data) {
								if (alertService.showResponseAlerts(data)) {
									pageData = data.page || pageData;
									updateGridPreview(pageData);
									updatePageStatus(pageData);
									return;
								}
								// widget needs configuration
								if (data.redirectLocation) {
									window.location = PROPERTY.baseUrl + data.redirectLocation.replace(/^\//, '');
									return;
								}

								// no need for configuration
								populateSlot($curSlot, $curWidget);

								// update local draft status
								pageData.draftWidgets[curSlotPos] = {
									config: null,
									type: {
										code: curWidgetType
									}
								};
								updatePageStatus(pageData);
							}
						});
					} else {

						var prevSlotPos = +$prevSlot.attr('data-pos');

						// move/swap the widget
						$.ajax(moveWidgetUrl, {
							method: 'POST',
							contentType: 'application/json',
							data: JSON.stringify({
								swapWidgetRequest: {
									pageCode: PROPERTY.code,
									src: prevSlotPos,
									dest: curSlotPos
								}
							}),
							success: function (data) {
								if (alertService.showResponseAlerts(data)) {
									pageData = data.page || pageData;
									updateGridPreview(pageData);
									updatePageStatus(pageData);
									return;
								}
								var $prevWidget = $curSlot.find('.grid-widget');
								setEmptySlot($prevSlot);
								setEmptySlot($curSlot);


								if (!_.isEmpty($prevWidget)) {
									var $otherWidget = createGridWidget($prevWidget.attr('data-widget-id'));
									populateSlot($prevSlot, $otherWidget);
								}

								var $newCurWidget = createGridWidget(curWidgetType);
								populateSlot($curSlot, $newCurWidget);

								// update local draft status
								var park = pageData.draftWidgets[curSlotPos];
								pageData.draftWidgets[curSlotPos] = pageData.draftWidgets[prevSlotPos];
								pageData.draftWidgets[prevSlotPos] = park;
								updatePageStatus(pageData);
							}
						});
					}

				}
			});

		} catch (e) {
			console.error(e, e.data);
			var alertText;
			switch (e.type) {
				case GridGenerator.ERROR.OVERLAPPING_FRAMES:
					alertText = getMessageText('error.grid.overlappingFrames',
						[e.data[0].a.description, e.data[0].b.description]);
					break;
				case GridGenerator.ERROR.MALFORMED_FRAMES:
					alertText = getMessageText('error.grid.malformedFrames', [e.data[0].description]);
					break;
				default:
					alertText = getMessageText('error.grid.gridError');
					break;

			}

			showGridWarning(alertText);
		}

	}


	function setDraggable(selector) {
		$(selector).draggable({
			helper: function () {
				var id = $(this).attr('data-widget-id');
				return $('.widget-square[data-widget-id="' + id + '"]').clone();
			},
			appendTo: 'body',
			cursorAt: {left: 30, top: 30},
			revert: 'invalid'
		});
	}


	/**
	 * On success, initializes pageData and then calls nextFunc if provided
	 * @param {Function} onSuccess a function to be called after the load succeeds
	 * @param {Function} onError a function to be called after the load fails
	 */
	function loadPageData(onSuccess, onError) {

		// Initializes page detail
		$.ajax(getPageDetailUrl, {
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				pageCode: PROPERTY.code
			}),
			success: function (data) {
				if (alertService.showResponseAlerts(data)) {
					return;
				}
				pageData = data.page;
				_.isFunction(onSuccess) && onSuccess(data);
			},
			error: function(data) {
				_.isFunction(onError) && onError(data);
			}
		});
	}


	/**
	 * On success, initializes apiMappings and then calls nextFunc if provided
	 * @param {Function} onSuccess a function to be called after the load succeeds
	 * @param {Function} onError a function to be called after the load fails
	 */
	function loadApiMappings(onSuccess, onError) {
		$.ajax(apiMappingsUrl, {
			method: 'POST',
			success: function (data) {
				if (alertService.showResponseAlerts(data)) {
					return;
				}
				apiMappings = data;
				_.isFunction(onSuccess) && onSuccess(data);
			},
			error: function(data) {
				_.isFunction(onError) && onError(data);
			}
		});
	}

	/**
	 * On success, initializes pageFrames and then calls nextFunc if provided
	 * @param {Function} onSuccess a function to be called after the load succeeds
	 * @param {Function} onError a function to be called after the load fails
	 */
	function loadPageFrames(onSuccess, onError) {
		// Initializes the grid
		$.ajax(serviceUrl, {
			method: 'GET',
			success: function (data) {
				if (alertService.showResponseAlerts(data)) {
					return;
				}
				pageFrames = data;
				_.isFunction(onSuccess) && onSuccess(data);
			},
			error: function (data) {
				_.isFunction(onError) && onError(data);
			}
		});
	}



	function handleApiError() {
		alertService.addDismissableError('Error fetching data');
	}


	/**
	 * Initializes all the page after loading all data
	 */
	function initPage() {
		loadPageData(function() {
			loadApiMappings(function() {
				loadPageFrames(function() {
					updatePageDetail(pageData);
					updateGridPreview(pageFrames);
					updatePageStatus(pageData);
				}, handleApiError);
			}, handleApiError);
		}, handleApiError);
	}


	if (PAGE_IS_SELECTED) {
		setDraggable($('.widget-square'), null);


		initPage();

		$('.restore-online-btn').click(function () {
			restoreOnlineConfig();
		});
		$('.publish-btn').click(function () {
			setPageOnlineStatus(true);
		});
		$('.unpublish-btn').click(function () {
			setPageOnlineStatus(false);
		});
	} else {
		$('#page-info, [data-target="#page-info"]').remove();
		$('.restore-online-btn').remove();
	}


});//domready