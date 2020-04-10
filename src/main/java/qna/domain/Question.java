package qna.domain;

import qna.CannotDeleteException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity {
    private static final String HAVE_NOT_PERMISSION_ERROR_MESSAGE =
            "질문을 삭제할 권한이 없습니다.";
    private static final String OTHER_ANSWER_EXIST_ERROR_MESSAGE =
            "다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.";
    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(long id, String title, String contents) {
        super(id);
        this.title = title;
        this.contents = contents;
    }

    public User getWriter() {
        return writer;
    }

    public Question writeBy(User loginUser) {
        this.writer = loginUser;
        return this;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public List<DeleteHistory> delete(User loginUser)
            throws CannotDeleteException {
        checkDeletable(loginUser);

        this.deleted = true;

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(),
                writer, LocalDateTime.now()));
        deleteHistories.addAll(answers.deleteAll());
        return deleteHistories;
    }

    private void checkDeletable(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException(HAVE_NOT_PERMISSION_ERROR_MESSAGE);
        }

        if (answers.hasOtherAnswers(loginUser)) {
            throw new CannotDeleteException(OTHER_ANSWER_EXIST_ERROR_MESSAGE);
        }
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" +
                contents + ", writer=" + writer + "]";
    }
}
