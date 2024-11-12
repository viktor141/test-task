AJS.toInit(function () {
    var dropdown = AJS.$("#issueTableDropdown");

    // Event listener for the Apply Selection button
    AJS.$(document).on('click', '#open-dialog-button', function() {
        var selectedOption = dropdown.find("option:selected").text();
        AJS.$("#selected-option").text(selectedOption);
        AJS.dialog2("#issue-table-dialog").show();
    });

    AJS.$(document).on('click', '#dialog-add-button', function() {
        var newName = AJS.$("#new-table-name").val(); // Assuming there's an input field in the dialog
        if(newName) {
            AJS.$.ajax({
                url: AJS.contextPath() + "/rest/tutorial-rest/latest/issue-table/add",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({ name: newName }),
                success: function(response) {
                    AJS.$("#issueTableDropdown").append(AJS.$("<option>").val(response.id).text(response.name).prop("selected", true));
                    AJS.dialog2("#issue-table-dialog").hide();
                    console.log("New table added: " + response.name);
                },
                error: function(xhr, status, error) {
                    console.error("Failed to add new table: " + error);
                }
            });
        } else {
            console.log("No name provided for the new table.");
        }
        AJS.dialog2("#issue-table-dialog").hide();
    });

    AJS.$(document).on('click', '#dialog-cancel-button', function() {
        AJS.dialog2("#issue-table-dialog").hide();
    });
});

