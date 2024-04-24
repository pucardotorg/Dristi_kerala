package digit.repository;


import digit.repository.querybuilder.CalendarQueryBuilder;
import digit.repository.rowmapper.CalendarRowMapper;
import digit.web.models.JudgeCalendar;
import digit.web.models.JudgeSearchCriteria;
import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CalendarRepository {

    @Autowired
    private CalendarQueryBuilder queryBuilder;

    @Autowired
    private CalendarRowMapper rowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<JudgeCalendar> getJudgeRule(JudgeSearchCriteria criteria) {
        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getJudgeCalendarQuery(criteria,preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query,rowMapper);
    }
}
