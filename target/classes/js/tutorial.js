//инициализация страниц jira
AJS.toInit(function () {

    //событие добавления нового типа контента на страницу jira
    JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {
        if (context && typeof context.attr !== 'undefined' && context.attr("id")) {

            console.log(" NEW_CONTENT_ADDED " + context.attr("id"));

            var elem = jQuery("#sourceVal-" + context.attr("id").replace("-val", ""));

            console.log(" elem " + elem.length + " : sourceVal-" + context.attr("id").replace("-val", ""));

            //если это наше поле - вызываем для него инициализацию
            if (elem.length > 0) {
                var cfId = elem.attr("id");
                setupTutorialCF(cfId);
            }
        }
    });

    //событие появления нового окна
    jQuery(document).bind('dialogContentReady', function (event, dialog) {
        jQuery(".tutorial-cf-source").each(function (index) {
            var cfId = jQuery(this).attr("id").replace("sourceVal-", "");

            console.log(" cfId " + cfId);
            console.log(" cfId field " + jQuery("#" + cfId + "-single-select").length);

            //если есть наше поле - вызываем для него инициализацию
            if (jQuery("#" + cfId + "-single-select").length < 1) {
                setupTutorialCF(cfId);
            }
        });
    });
});

//построение копмонента списка для поля
function setupTutorialCF(customfieldId) {
    var customfieldId = customfieldId.replace("sourceVal-", "");

    console.log("setupTutorialCF " + customfieldId);

    var sourceCFId = jQuery("#sourceVal-" + customfieldId).val();
    var sourceVal = jQuery("#" + sourceCFId).val();

    //если поля нет на экране - выход
    if (jQuery("#" + customfieldId).length < 1) {
        return;
    }

    //получаем значения из админки поля
    jQuery.ajax({
        method: "get",

        //паарметры вызова
        data: {
            customfieldId: customfieldId,
            sourceCFId: sourceCFId,
            sourceVal: sourceVal
        },
        dataType: "json",

        //путь к REST
        url: contextPath + "/rest/tutorial-rest/latest/tutorial/initCf",

        success: function (response, textStatus, jqXHR) {
            var el = jQuery("#" + response.customfieldId);

            console.log(" initCf " + el + " : " + response.customfieldId);

            //если компонент уже есть - выход
            if (jQuery("#" + el.attr("id") + "-single-select").length > 0) {
                return;
            }

            console.log(" cfId initCf " + jQuery("#" + response.customfieldId + "-single-select").length);

            //компонент списка
            new AJS.SingleSelect({

                //запрещаем сводобный ввод имени поля
                handleFreeInput: function (value) {
                },

                //запрещаем вывод ошибок
                errorMessage: "",

                //ссылка на html список
                element: el,

                //режим отображения
                itemAttrDisplayed: "label",

                //показать кнопку выбора
                showDropdownButton: true,

                //параметры вызова REST
                ajaxOptions: {
                    data: function (query) {
                        return {
                            sourceCFId: response.sourceCFId,
                            sourceVal: jQuery("#" + response.sourceCFId).val(),
                            customfieldId: response.customfieldId,
                            query: query == "" && el.text() != 'Not defined' ? el.text() : query
                        }
                    },

                    //путь к REST для поиска пунктов списка
                    url: contextPath + "/rest/tutorial-rest/latest/tutorial/searchCfRecords",

                    query: true,

                    //готовим список для отображения
                    formatResponse: function (data) {
                        var ret = [];

                        //по данным с REST
                        jQuery(data).each(function (i, suggestions) {
                            var groupDescriptor = new AJS.GroupDescriptor({
                                weight: i, // order or groups in suggestions dropdown
                                label: suggestions.header
                            });

                            //новый элемет списка
                            jQuery(suggestions.groups).each(function () {
                                groupDescriptor.addItem(new AJS.ItemDescriptor({
                                    value: this.name, // value of item added to select
                                    label: this.html, // title of lozenge
                                    html: this.html,
                                    highlighted: true
                                }));
                            });

                            ret.push(groupDescriptor);
                        });

                        return ret;
                    }
                }
            });

            //при изменении значения списка - не делать ничего
            el.change(function () {
                //todo
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        }
    });
}