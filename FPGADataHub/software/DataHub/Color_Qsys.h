#ifndef COLOR_QSYS_H_
#define COLOR_QSYS_H_
#include "../Datahub_bsp/system.h"
#include "system.h"

struct RGB{
	unsigned int RValue;
	unsigned int GValue;
	unsigned int BValue;
	int isValid;
};

struct RGB GetColor();

#endif
