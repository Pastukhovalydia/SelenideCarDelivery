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

        // Оставляем поле "Фамилия и Имя" пустым, заполняем остальные поля корректно и устанавливаем чекбокс
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=phone] input").setValue("+79114445566");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
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

        // Указываем город за пределами РФ, устанавливаем чекбокс
        $("[data-test-id=city] input").setValue("Астана");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Доставка в выбранный город недоступна"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldShowErrorForEmptyNameWithCheckedAgreement() {
        open("http://localhost:9999");

        // Оставляем поле "Фамилия и Имя" пустым, устанавливаем чекбокс
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForEmptyPhoneWithCheckedAgreement() {
        open("http://localhost:9999");

        // Оставляем поле "Телефон" пустым, устанавливаем чекбокс
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем сообщение
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


    @Test
    void shouldShowErrorForUncheckedAgreement() {
        open("http://localhost:9999");

        // Заполняем все поля корректно, не устанавливаем чекбокс
        $("[data-test-id=city] input").setValue("Москва");
        LocalDate planningDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate.format(formatter));
        $("[data-test-id=name] input").setValue("Пастухова Лидия");
        $("[data-test-id=phone] input").setValue("+79114445566");
        $$("button").find(exactText("Забронировать")).click();

        // Проверяем, что текст "Я соглашаюсь с условиями..." подсвечен красным
        $(".checkbox__text").shouldHave(cssValue("color", "rgba(255, 92, 92, 1)"), Duration.ofSeconds(5));
    }

}



