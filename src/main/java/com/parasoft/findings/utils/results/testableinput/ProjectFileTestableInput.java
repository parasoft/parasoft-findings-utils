package com.parasoft.findings.utils.results.testableinput;

import com.parasoft.findings.utils.common.util.StringUtil;
import com.parasoft.findings.utils.common.util.ObjectUtil;

import java.io.File;

public class ProjectFileTestableInput extends FileTestableInput
        implements IFileTestableInput, IProjectTestableInput {

    private final String _sProjectId;

    private final String _sProjectName;

    private final String _sResProjRelatPath;

    private final String _sProjectPath;

    public ProjectFileTestableInput(File file, String sProjectId, String sProjectName, String sProjectPath, String sResProjRelatPath) {
        super(file);
        _sProjectId = sProjectId;
        _sProjectName = sProjectName;
        _sResProjRelatPath = sResProjRelatPath;
        _sProjectPath = sProjectPath;
    }

    @Override
    public String getProjectName() {
        return _sProjectName;
    }

    @Override
    public String getProjectPath() {
        return _sProjectPath;
    }

    @Override
    public String getProjectRelativePath() {
        return _sResProjRelatPath;
    }

    @Override
    public int hashCode() {
        return (((ObjectUtil.hashCode(_sProjectId) * 31) + ObjectUtil.hashCode(_sResProjRelatPath)) * 31) + super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProjectFileTestableInput)) {
            return false;
        }
        ProjectFileTestableInput input = (ProjectFileTestableInput) obj;
        return super.equals(obj) && StringUtil.equals(_sProjectId, input._sProjectId) &&
                StringUtil.equals(_sResProjRelatPath, input._sResProjRelatPath);
    }
}
