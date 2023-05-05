package com.minibyte.bo.pojo.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocDto {
    private Term term;
    private Document document;
}
