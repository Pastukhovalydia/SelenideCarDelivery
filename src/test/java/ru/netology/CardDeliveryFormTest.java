package ru.netology;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryFormTest {

    @Test
    void shouldSubmitCardDeliveryFormWithCorrectData() {
        open("http://localhost:9999");

        //Успешная отправка заявки
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!"))
                .shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .shouldHave(text("Встреча успешно забронирована на " + planningDate.format(formatter)),
                        Duration.ofSeconds(15))
                .shouldBe(visible);
    }

    @Test
    void shouldShowErrorForPastDate() {
        open("http://localhost:9999");

        //Устанавливаем прошедшую дату
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate pastDate = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(pastDate.format(formatter));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Заказ на выбранную дату невозможен"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldShowErrorForEmptyCity() {
        open("http://localhost:9999");

        // Оставляем поле "Город" пустым
        $("[data-test-id=city] input").setValue("");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForEmptyNameWithOtherFieldsFilledCorrectly() {
        open("http://localhost:9999");

        // Поле Фамилия и Имя на латинице
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=name] input").setValue("Gena");
        $("[data-test-id=phone] input").setValue("+79114445566");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForInvalidPhone() {
        open("http://localhost:9999");

        // Вводим некорректный телефон
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");

        $("[data-test-id=phone] input").setValue("+7911");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $(withText("Телефон указан неверно"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForUnavailableCity() {
        open("http://localhost:9999");

        // Указываем город за пределами РФ , заполняем остальные поля корректно и устанавливаем чекбокс
        $("[data-test-id=city] input").setValue("Астана");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");

        // Устанавливаем чекбокс
        $("[data-test-id=agreement]").click();

        // Нажимаем кнопку "Забронировать"
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Доставка в выбранный город недоступна"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForEmptyNameWithCheckedAgreement() {
        open("http://localhost:9999");

        // Заполняем все поля, кроме Фамилии и Имени
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=phone] input").setValue("+79114445566");

        // Оставляем поле Фамилии и Имени пустым
        $("[data-test-id=name] input").setValue("");

        // Устанавливаем чекбокс
        $("[data-test-id=agreement]").click();

        // Нажимаем кнопку "Забронировать"
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForEmptyPhoneWithCheckedAgreement() {
        open("http://localhost:9999");

        // Заполняем все поля, кроме телефона
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");

        // Оставляем поле телефона пустым
        $("[data-test-id=phone] input").setValue("");

        // Устанавливаем чекбокс
        $("[data-test-id=agreement]").click();

        // Нажимаем кнопку "Забронировать"
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForEmptyFieldsAndCheckedAgreement() {
        open("http://localhost:9999");

        // Не заполняем обязательные поля
        $("[data-test-id=city] input").setValue("");

        // Очищаем поле с датой
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);

        $("[data-test-id=name] input").setValue("");
        $("[data-test-id=phone] input").setValue("");

        // Устанавливаем чекбокс
        $("[data-test-id=agreement]").click();

        // Нажимаем кнопку "Забронировать"
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldShowErrorForEmptyDate() {
        open("http://localhost:9999");

        // Заполняем все поля, кроме поля"Дата"
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(""); // Очищаем поле "Дата"
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");

        // Устанавливаем чекбокс
        $("[data-test-id=agreement]").click();

        // Нажимаем кнопку "Забронировать"
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Неверно введена дата"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

}



