$(document).ready(function() {
    const host = "http://localhost:8080/currency"

    function populateAddCurrencyDropdown() {
        $.ajax({
            url: `${host}/allcurrencies`,
            type: "GET",
            dataType: "json",
            success: function (data) {
                const dropdown = $("#add-currency-select");
                let optionsHtml = '<option value="" disabled selected>Select currency...</option>';
                $.each(data, function (index, /** @type {{code: string, name: string, symbol: string}} */ currency) {
                    optionsHtml += `<option value="${currency.code}" data-name="${currency.name}" data-symbol="${currency.symbol}">${currency.code} - ${currency.name} (${currency.symbol})</option>`;
                });
                dropdown.html(optionsHtml);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Ошибка при загрузке валют:", jqXHR, textStatus, errorThrown);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text('Не удалось загрузить полный список валют.');
                toast.toast("show");
            }
        });
    }

    function requestCurrencies() {
        $.ajax({
            url: `${host}/currencies`,
            type: "GET",
            dataType: "json",
            success: function (data) {
                const tbody = $('.currencies-table tbody');
                tbody.empty();
                $.each(data, function(index, currency) {
                    const row = $('<tr></tr>');
                    row.append($('<td></td>').text(currency.code));
                    row.append($('<td></td>').text(currency.name));
                    row.append($('<td></td>').text(currency.sign));
                    tbody.append(row);
                });

                const newRateBaseCurrency = $("#new-rate-base-currency");
                newRateBaseCurrency.empty();
                $.each(data, function (index, currency) {
                    newRateBaseCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const newRateTargetCurrency = $("#new-rate-target-currency");
                newRateTargetCurrency.empty();
                $.each(data, function (index, currency) {
                    newRateTargetCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const convertBaseCurrency = $("#convert-base-currency");
                convertBaseCurrency.empty();
                $.each(data, function (index, currency) {
                    convertBaseCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });

                const convertTargetCurrency = $("#convert-target-currency");
                convertTargetCurrency.empty();
                $.each(data, function (index, currency) {
                    convertTargetCurrency.append(`<option value="${currency.code}">${currency.code}</option>`);
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
    }

    populateAddCurrencyDropdown();
    requestCurrencies();

    $("#add-currency").submit(function(e) {
        e.preventDefault();
        const selectedOption = $("#add-currency-select").find(":selected");
        if (!selectedOption.val()) {
            alert("Select currency ...");
            return;
        }
        const currencyName = selectedOption.data("name");
        const currencySymbol = selectedOption.data("symbol");
        $("#add-currency-name-hidden").val(currencyName);
        $("#add-currency-sign-hidden").val(currencySymbol);
        const dataToSend = $("#add-currency").serialize();
        $.ajax({
            url: `${host}/currencies`,
            type: "POST",
            data: dataToSend,
            success: function(data) {
                requestCurrencies();
                requestExchangeRates();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
        return false;
    });

    function requestExchangeRates() {
        $.ajax({
            url: `${host}/exchangeRates`,
            type: "GET",
            dataType: "json",
            success: function(response) {
                const tbody = $('.exchange-rates-table tbody');
                tbody.empty();
                $.each(response, function(index, rate) {
                    const row = $('<tr></tr>');
                    const currency = rate.baseCurrency.code + rate.targetCurrency.code;
                    const exchangeRate = rate.rate;
                    row.append($('<td></td>').text(currency));
                    row.append($('<td></td>').text(exchangeRate));
                    row.append($('<td></td>').html(
                        '<button class="btn btn-secondary btn-sm exchange-rate-edit"' +
                        'data-bs-toggle="modal" data-bs-target="#edit-exchange-rate-modal">Edit</button>'
                    ));
                    tbody.append(row);
                });
            },
            error: function(jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
    }

    requestExchangeRates();

    $(document).delegate('.exchange-rate-edit', 'click', function() {
        const pair = $(this).closest('tr').find('td:first').text();
        const exchangeRate = $(this).closest('tr').find('td:eq(1)').text();
        $('#edit-exchange-rate-modal .modal-title').text(`Edit ${pair} Exchange Rate`);
        $('#edit-exchange-rate-modal #exchange-rate-input').val(exchangeRate);
    });

    $('#edit-exchange-rate-modal .btn-primary').click(function() {
        const pair = $('#edit-exchange-rate-modal .modal-title').text().replace('Edit ', '').replace(' Exchange Rate', '');
        const exchangeRate = $('#edit-exchange-rate-modal #exchange-rate-input').val();
        $.ajax({
            url: `${host}/exchangeRate/${pair}`,
            type: "PATCH",
            contentType : "application/x-www-form-urlencoded",
            data: `rate=${exchangeRate}`,
            success: function() {
                const row = $(`tr:contains(${pair})`);
                row.find('td:eq(1)').text(exchangeRate);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
        $('#edit-exchange-rate-modal').modal('hide');
    });

    $("#add-exchange-rate").submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: `${host}/exchangeRates`,
            type: "POST",
            data: $("#add-exchange-rate").serialize(),
            success: function(data) {
                requestExchangeRates();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
        return false;
    });

    $("#convert").submit(function(e) {
        e.preventDefault();
        const baseCurrency = $("#convert-base-currency").val();
        const targetCurrency = $("#convert-target-currency").val();
        const amount = $("#convert-amount").val();
        $.ajax({
            url: `${host}/exchange?from=${baseCurrency}&to=${targetCurrency}&amount=${amount}`,
            type: "GET",
            success: function(data) {
                $("#convert-converted-amount").val(data.convertedAmount);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                const error = JSON.parse(jqXHR.responseText);
                const toast = $('#api-error-toast');
                $(toast).find('.toast-body').text(error.message);
                toast.toast("show");
            }
        });
        return false;
    });
});