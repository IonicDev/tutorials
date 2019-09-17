#pragma once
#include <ISAgent.h>
#include <ISLog.h>
#include <string>
#include <vector>


class BulkEncrypt {
public:
	BulkEncrypt(ISAgentKeyServices& keyServices);
	~BulkEncrypt();

	void setAttributes(const ISKeyAttributesMap & mapKeyAttributes);
	void setMutableAttributes(const ISKeyAttributesMap & mapMutableKeyAttributes);
	void setMetadata(const ISMetadataMap & mapMetadata);

	int encrypt(std::vector<std::string> filenames, std::string outputPath, bool haltOnError = true);
	int decrypt(std::vector<std::string> filenames, std::string outputPath, bool haltOnError = true);

	static const std::string LOGCHANNEL;
	enum ErrorCode {
		OK = 0,
		INPUT_ERROR = 1001
	};


private:
	ISAgentKeyServices & m_services;
	ISKeyAttributesMap m_mapKeyAttributes;
	ISKeyAttributesMap m_mapMutableKeyAttributes;
	ISMetadataMap m_mapMetadata;
};

