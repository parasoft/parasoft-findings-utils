package com.parasoft.findings.utils.results.violations;

import com.parasoft.findings.utils.results.xml.RulesImportHandler;
import com.parasoft.findings.utils.results.xml.XmlReportReader;
import com.parasoft.findings.utils.common.util.IOUtils;
import com.parasoft.findings.utils.common.util.XMLUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class XmlReportViolations implements Iterator<IViolation> {

    private XmlReportReader _reportReader = null;

    private Iterator<IViolation> _violationsIter;

    protected XmlReportViolations(URL reportURL, XmlReportReader reportReader) {
        _reportReader = reportReader;
        InputStream is = null;
        try {
            is = reportURL.openStream();
            SAXParser parser = XMLUtil.createSAXParser();
            parser.parse(is, _reportReader);
            _violationsIter = _reportReader.getImportedViolations().iterator();
        } catch (ParserConfigurationException | SAXException | IOException ex) { // parasoft-suppress OWASP2021.A9.LGE "This is intentionally designed to ensure exceptions during XML report violations parsing don't cause the process to fail."
            Logger.getLogger().error(ex);
        } finally {
            IOUtils.close(is);
        }
    }

    public RulesImportHandler getRulesImportHandler() {
        return _reportReader.getRulesImportHandler();
    }

    @Override
    public boolean hasNext() {
        return (_violationsIter != null) && _violationsIter.hasNext();
    }

    @Override
    public IViolation next() {
        if (_violationsIter == null) {
            throw new NoSuchElementException();
        }
        IViolation violation = _violationsIter.next();
        return violation;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
