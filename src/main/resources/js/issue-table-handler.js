AJS.toInit(function () {
    var dropdown = AJS.$("#issueTableDropdown");

    // Event listener for the Apply Selection button
    AJS.$(document).on('click', '#open-dialog-button', function() {
        var selectedOption = dropdown.find("option:selected").text();
        AJS.$("#selected-option").text(selectedOption);
        AJS.dialog2("#issue-table-dialog").show();
    });

    AJS.$(document).on('click', '#dialog-confirm-button', function() {
        var selectedValue = dropdown.val();
        console.log("Confirmed selection: " + selectedValue);
        AJS.dialog2("#issue-table-dialog").hide();
    });

    AJS.$(document).on('click', '#dialog-cancel-button', function() {
        AJS.dialog2("#issue-table-dialog").hide();
    });
});

