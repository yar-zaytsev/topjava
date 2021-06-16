package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("by streams:");
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();

        List<UserMeal> filteredList = new ArrayList<>();

        for (UserMeal userMeal : meals) {

            if (!caloriesPerDayMap.containsKey(userMeal.getDateTime().toLocalDate())) {
                caloriesPerDayMap.put(userMeal.getDateTime().toLocalDate(), userMeal.getCalories());
            } else {
                Integer caloriesPerDaySumm = caloriesPerDayMap.get(userMeal.getDateTime().toLocalDate());
                caloriesPerDaySumm += userMeal.getCalories();
                caloriesPerDayMap.put(userMeal.getDateTime().toLocalDate(), caloriesPerDaySumm);
            }
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredList.add(new UserMeal(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories()));
            }

        }

        List<UserMealWithExcess> filteredListWithExcees = new ArrayList<>();
        for (UserMeal userMeal : filteredList) {
            int calories = caloriesPerDayMap.get(userMeal.getDateTime().toLocalDate());
            boolean exceed = false;

            if (calories > caloriesPerDay) {
                exceed = true;
            }

            filteredListWithExcees.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), exceed));
        }
        return filteredListWithExcees;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime
            startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> caloriesByAllDay = meals.stream().collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(meal -> meal.getCalories())));
        List<UserMealWithExcess> filteredMealsWithExceed = meals.stream().filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), caloriesByAllDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());

        return filteredMealsWithExceed;
    }
}

