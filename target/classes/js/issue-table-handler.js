/*AJS.toInit(function() {
/!*    var dropdown = AJS.$("#issueTableDropdown");

    // Load dropdown options from the server
    AJS.$.ajax({
        url: AJS.contextPath() + "/rest/tutorial-rest/latest/issue-table/tables",
        type: "GET",
        dataType: "json",
        success: function(response) {
            response.forEach(function(item) {
                dropdown.append(AJS.$("<option>").val(item.id).text(item.name));
            });
        },
        error: function(error) {
            console.error("Failed to load issue table data:", error);
        }
    });*!/
    // Shows the dialog when the "Show dialog" button is clicked
    AJS.$(document).on('click', '#open-dialog-button', function() {
        console.log("Open dialog button clicked.");
        AJS.dialog2("#demo-dialog").show();
    });


/!*    AJS.$("#open-dialog-button").on('click', function(e) {
        e.preventDefault();
        console.log("clickeddd")
        AJS.dialog2("#demo-dialog").show();
    });*!/

// Hides the dialog
    AJS.$("#dialog-submit-button").on('click', function (e) {
        e.preventDefault();
        AJS.dialog2("#demo-dialog").hide();
    });

    /!*!// Open dialog when the button is clicked
    AJS.$("#open-dialog-button").click(function() {
        console.log("Setting selection: ");
        AJS.dialog2("#issue-table-dialog").show();
    });*!/

    /!*!// Apply button action
    AJS.$("#dialog-apply-button").click(function() {
        var selectedValue = dropdown.val();
        console.log("Applying selection: ");
        // Additional logic to apply the selected value goes here
        AJS.dialog2("#issue-table-dialog").hide();
    });

    // Close dialog on button click
    AJS.$("#dialog-close-button").click(function() {
        AJS.dialog2("#issue-table-dialog").hide();
    });*!/
});*/

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

