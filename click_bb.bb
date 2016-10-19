;******************************************************************************
;* Project: Clickomania Deluxe - Blitz Version
;* Dev. by: © 2004 Flaith
;* translated from program in C++ by Sergey Pozhilov for testing HGE
;******************************************************************************

AppTitle "ClickoMania Deluxe - Blitz version 0.8"
Graphics 640,480,16,2
SetBuffer BackBuffer ()

HidePointer

SeedRnd (MilliSecs ())

Global SCREEN_WIDTH	=	640
Global SCREEN_HEIGHT=	480
Global CELL_COLUMNS	=	16
Global CELL_ROWS	=	11
Global CELL_WIDTH	=	39
Global CELL_HEIGHT	=	39
Global NBLEFT		=	0
Global SCORE		=	0
Global NBTOTAL		=	0
Global x_left		=	-255
Global y_top		=	0
Global BONUS_COL	=	False 
Global FIND			=	0
Global VAL_FIND		=	0
Global debug		=	False

Global NB_CELL_COLUMNS

Global blocs = LoadAnimImage ("res\cells.png",CELL_WIDTH,CELL_HEIGHT,0,5) : MaskImage blocs,255,0,255
If blocs=0 Then RuntimeError "File "+Chr(34)+"CELLS.PNG"+Chr(34)+" not found!"
Global scorebar = LoadImage ("res\scorebar.png")
Global background = LoadImage ("res\bg.png")
Global cursor = LoadImage ("res\mouse.png") : MaskImage cursor,255,0,255

Global boom = LoadSound ("res\boom.wav")

Global fntArialA=LoadFont("Arial",12)
Global fntArialB=LoadFont("Arial",24,True)
SetFont fntArialB

Type MousePos
	Field X
	Field Y
	Field col
	Field row
	Field click
End Type

Global PosXY.MousePos

;******************************************************************************
Dim cells(CELL_COLUMNS-1,CELL_ROWS-1)
Dim active_Cell(CELL_COLUMNS-1,CELL_ROWS-1)

frameTimer=CreateTimer(60) 

CreateGrid()

While Not KeyDown (1)  ; Esc
Cls
	WaitTimer(frameTimer)
	UpdateBackground()
	DrawCells()
	ShowMouse()
	CheckMouse()
	ViewScore()
	DelMousePos()
	;CheckActive()
	;If CheckRemainCells()=NBLEFT Then Text 10,150,"NO MORE CELLS !!!"
	;Text 10,150,"Check : "+CheckRemainCells()
Flip 
Wend

;******************************************************************************

Function CreateGrid()
	For row=0 To CELL_ROWS-1
		For col=0 To CELL_COLUMNS-1
			cells(col,row) = Rand(0,4)
		Next
	Next
	NBLEFT = CELL_ROWS * CELL_COLUMNS
	NBTOTAL = 0
	SCORE = 0
	BONUS_COL = False
	NB_CELL_COLUMNS = 0
End Function 

Function GetMousePos.MousePos()
	MOUSEPOS.MousePos = New MousePos
	MOUSEPOS\X = MouseX ()
	MOUSEPOS\Y = MouseY ()

	If MouseHit(1) Then MOUSEPOS\click = 1
	If MouseHit(2) Then MOUSEPOS\click = 2
	If MouseHit(3) Then MOUSEPOS\click = 3

	Return MOUSEPOS
End Function 
	
Function DelMousePos.MousePos()
	Delete MOUSEPOS.MousePos
End Function 

Function ShowMouse()
	x = MouseX ()
	y = MouseY ()
	If x >= (SCREEN_WIDTH - 14) Then x = (SCREEN_WIDTH - 14)
	If y >= (SCREEN_HEIGHT - 16 - 40) Then y = (SCREEN_HEIGHT - 16 - 40)
	
	DrawImage cursor,x,y
End Function

Function CheckMouse()
	Select PosXY\click
		Case 1 : DeleteActive()		;left click
		Case 2 : CreateGrid()		;right click
	End Select
End Function

Function DrawCells()
	GetCurrenCell()
	col=PosXY\Col
	row=PosXY\row

	zero_active_cell()
	
	FIND=0
	FIND=FindNeighbours(col, row, cells(col,row))
	For col=0 To CELL_COLUMNS-1
		For row=0 To CELL_ROWS-1
			Rand0 = 0
			Rand1 = 0
			If active_cell(col,row) = 1
				Rand0 = Rand(0,3)-1
				Rand1 = Rand(0,3)-1
			EndIf
			c = cells(col,row)
			If c >=0 Then
				DrawImage blocs,col*(CELL_WIDTH+1)+Rand0,row*(CELL_HEIGHT+1)+Rand1,cells(col,row)
			EndIf
		Next
	Next
End Function 

Function CheckRemainCells()
	FIND=0
	For col=0 To CELL_COLUMNS-1
		For row=0 To CELL_ROWS-1
			FIND=FindNeighbours(col, row, cells(col,row))
		Next
	Next
	Return FIND
End Function

Function GetCurrenCell()
	PosXY.MousePos = GetMousePos()
	c = PosXY\X/(CELL_WIDTH+1)
	r = PosXY\Y/(CELL_HEIGHT+1)

	If c > CELL_COLUMNS-1 Then c = CELL_COLUMNS-1
	If r > CELL_ROWS-1 Then r = CELL_ROWS-1

	PosXY\col = c
	PosXY\row = r
End Function 

Function FindNeighbours(col, row, colorcell)
	c = cells(col,row)
	If (c = colorcell) And (active_cell(col,row) = False)
		active_cell(col,row) = 1
		If (col>0) FindNeighbours(col-1, row, Colorcell)
		If (col<CELL_COLUMNS-1) FindNeighbours(col+1, row, Colorcell)
		If (row>0) FindNeighbours(col, row-1, Colorcell)
		If (row<CELL_ROWS-1) FindNeighbours(col, row+1, Colorcell)
		FIND = FIND + 1
	EndIf
	Return FIND
End Function 

Function zero_active_cell()
	For row=0 To CELL_ROWS-1
		For col=0 To CELL_COLUMNS-1
			active_cell(col,row)=0
		Next
	Next
End Function

Function MoveEmptyUp()
	For col=0 To CELL_COLUMNS-1
		For k=CELL_ROWS-2 To 0 Step -1
			For row=k To CELL_ROWS-2
				If cells(col,row+1)= -1 Then 
					cells(col,row+1) = cells(col,row)
					cells(col,row) = -1
				EndIf
			Next 
		Next 
	Next 
End Function 

Function IsColumnEmpty(col)
	result = True 
	For row=0 To CELL_ROWS-1
		If cells(col,row) <> -1 result = False 
	Next 
	Return result
End Function 

Function SwapColumns(col1, col2)
	Local Colour = 0
	For row=0 To CELL_ROWS-1
		Colour = cells(col1,row)
		cells(col1,row) = cells(col2,row)
		cells(col2,row) = Colour
	Next
End Function

Function MoveEmptyRight()
	For k=0 To CELL_COLUMNS-2
		For col=CELL_COLUMNS-1 To k+1 Step -1
			If (IsColumnEmpty(col)) Then
				SwapColumns(col, col-1)
			EndIf
		Next 
	Next
End Function 

Function CheckActiveCol()
	test=False
	result=False 
	For col=NB_CELL_COLUMNS To CELL_COLUMNS-1
		If IsColumnEmpty(col) Then test = True
	Next
	If test Then
		NB_CELL_COLUMNS = NB_CELL_COLUMNS + 1
		Result=True
	EndIf
	Return result
End Function 

Function CheckActive()
	Local col, row
	Local actives = 0

	For col=0 To CELL_COLUMNS-1
		For row=0 To CELL_ROWS-1
			If cells(col,row) <> -1 actives=actives+1
		Next
	Next 

	Text 150,457,"Actives = "+actives

	;Delete actives (set the Color = -1)
	If actives < 2 Then Text 300,150,"THE END !"
End Function 

Function DeleteActive()
	BONUS_COL=False 
	Local col, row
	Local actives = 0

	;check number of actives (must be >2)
	For col=0 To CELL_COLUMNS-1
		For row=0 To CELL_ROWS-1
			If active_cell(col,row) And cells(col,row) <> -1 actives=actives+1
		Next
	Next 

	;Delete actives (set the Color = -1)
	If actives > 1
	NBTOTAL = actives
		;play sound
		PlaySound(boom)
		For col=0 To CELL_COLUMNS-1
			For row=0 To CELL_ROWS-1
				If active_cell(col,row)
					cells(col,row) = - 1
					NBLEFT = NBLEFT - 1				;nbre restant de cube
				EndIf 
			Next
		Next 

		;move empty cells up
		MoveEmptyUp()
		;move empty columns Right
		MoveEmptyRight()
	EndIf

	If CheckActiveCol() Then BONUS_COL=True 

	;clear actives
	Zero_active_cell()
End Function 

Function UpdateBackground()
	x_steps = (SCREEN_WIDTH/256)+2
	y_steps = (SCREEN_HEIGHT/256)+2

	If x_left>=-1 Then x_left=-255 Else x_left=x_left+1
	If y_top<=-254 Then y_top=0 Else y_top=y_top-1

	For i=0 To x_steps-1
		For j=0 To y_steps-1
			DrawBlock background,x_left+i*256,y_top+j*256
		Next
	Next 
End Function 

Function ViewScore()
	Color 0,0,0
	DrawBlock scorebar,0,CELL_ROWS*(CELL_HEIGHT+1)

	If Debug 
		SetFont fntArialA
		Color 128,128,128
		Text 10,445,"X = "+PosXY\X+" - Y = "+PosXY\Y
		Text 10,455,"row = "+PosXY\row+" - col = "+PosXY\col
		Select cells(PosXY\col,PosXY\row)
			Case 0:txt$="GREEN"
			Case 1:txt$="BLUE"
			Case 2:txt$="RED"
			Case 3:txt$="YELLOW"
			Case 4:txt$="PURPLE"
			Default:txt$="BLACK"
		End Select 
		Text 10,465,"color = "+txt$
	EndIf 

	Local value = 0
	If NBTOTAL > 0
		If NBTOTAL >=2 And NBTOTAL <= 3 Then value = NBTOTAL * 2
		If NBTOTAL >3 And NBTOTAL <= 5 Then value = NBTOTAL * 3
		If NBTOTAL >5 And NBTOTAL <= 8 Then value = NBTOTAL * 5
		If NBTOTAL >8 Then value = NBTOTAL * 10
	SCORE = SCORE + (value*10)
	If BONUS_COL Then SCORE = SCORE + 2500
	EndIf 

	SetFont fntArialA
	Color 255,255,255
	Text PosXY\X+10,PosXY\Y+10,FIND + " Cells"
;	Text 150,440,"LEFT CELLS = "
;	Text 450,450,"SCORE = "
	SetFont fntArialB
	Color 96,250,255
	Text 580,440,SCORE
	Text 580,457,NBLEFT
;	Text 150,440,"NB CELLS = "+FIND
	Text 150,457,"NB CELLS COL= "+NB_CELL_COLUMNS

;	Text 300,150,"BONUS = "+BONUS_COL

	NBTOTAL = 0
End Function 
