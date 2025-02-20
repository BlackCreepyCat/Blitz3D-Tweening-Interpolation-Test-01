; ------------------------------
; Nombre d'image MAX
; ------------------------------
Global AppFPS =75

; ---------------------------
; Ouverture de l'ecran
; ---------------------------
Graphics3D 1024, 768,32,2
SetBuffer BackBuffer ()

; ---------------------------------
; Changement de la fonte
; ---------------------------------
AppFont=LoadFont("Arial",15,1,0,0) 
SetFont AppFont

; ----------------------------------
; Construction de la scene
; ----------------------------------
MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
AmbientLight 100,100,100

; Créer un cube
Global cube = CreateCube()

; Position de départ et d'arrivée
Global startX# = -10
Global startY# = 0
Global startZ# = 0
Global endX# = 10
Global endY# = 0
Global endZ# = 0

Global Camera = CreateCamera()
PositionEntity Camera, 0, 0, -15

; Temps de déplacement
Global moveTime# = 8.0  ; Temps total pour le déplacement (en secondes)
Global startTime# = MilliSecs()

; Définir l'état initial de l'animation (vers la destination ou inverse)
Global isForward% = 1
Global t# = 0.0  ; Initialisation du facteur t# à 0


; Calculer le temps écoulé en secondes depuis le début de l'animation
Global elapsedTime# = (MilliSecs() - startTime#) / 1000.0  ; Conversion en secondes

; -----------------------------------------------
;	Boucle principale du programme
; -----------------------------------------------
FramePeriod = 1000 / 25
FrameTime = MilliSecs () - FramePeriod

Repeat
	; ----------------------------
	; On limite les frames
	; ----------------------------
	Repeat
		FrameElapsed = MilliSecs () - FrameTime
	Until FrameElapsed
	
	FrameTicks = FrameElapsed / FramePeriod
	FrameTween# = Float (FrameElapsed Mod FramePeriod) / Float (FramePeriod)
	
	; ------------------------------------------------------
	; On update les monde et les animations
	; ------------------------------------------------------
	For FrameLimit = 1 To FrameTicks
		If FrameLimit = FrameTicks Then
			CaptureWorld
		EndIf
		
		FrameTime = FrameTime + FramePeriod
		
		UpdateGame ()
		UpdateWorld
	Next
	
	
	; On redraw la scene
	RenderWorld FrameTween
	
	; Affichage des FPS
	Color 255,255,255
	;Text 10,10,Get_FPS()+" FPS"
	
	Flip
Until KeyHit (1)

Function CalculateT#(AnimTime#, PinPong% = False)
    elapsedTime# = (MilliSecs() - startTime#) / 1000.0  ; Temps écoulé en secondes
    totalCycle# = AnimTime# * 2  ; Cycle complet aller-retour
    
    ; Calcul de t# pour l'animation, bouclage après chaque cycle complet
    t# = (elapsedTime# Mod totalCycle#) / AnimTime#  ; De 0 à 1 pendant un aller complet
    
    If PinPong% = True Then
        ; Inverser la direction après avoir atteint 1.0
        If t# > 1.0 Then
            t# = 2.0 - t#  ; Inversion pour le retour
        EndIf
    Else
        ; Pour PinPong = False, on boucle directement
        If t# > 1.0 Then
            t# = 1.0  ; Ne jamais dépasser 1.0
        EndIf
    EndIf
    
    Return t#
End Function

Function UpdateGame ()
    ; Calculer le facteur t# basé sur le temps écoulé et le temps de déplacement
    t# = CalculateT#(moveTime#, True)  ; True pour PingPong
    
;    ; Calculer les positions du cube avec l'interpolation
;    If isForward% = 1 Then
;        currentX# = Tween_InOutQuad#(t#, startX#, endX# - startX#, 1)
;        currentY# = Tween_InOutQuad#(t#, startY#, endY# - startY#, 1)
;        currentZ# = Tween_InOutQuad#(t#, startZ#, endZ# - startZ#, 1)
;    Else
;        currentX# = Tween_InOutQuad#(1 - t#, startX#, endX# - startX#, 1)  ; Interpolation inverse
;        currentY# = Tween_InOutQuad#(1 - t#, startY#, endY# - startY#, 1)
;        currentZ# = Tween_InOutQuad#(1 - t#, startZ#, endZ# - startZ#, 1)
;    EndIf
	
;    ; Calculer les positions du cube avec l'interpolation
;    If isForward% = 1 Then
;        currentX# = Tween_OutInCubic#(t#, startX#, endX# - startX#, 1)
;        currentY# = Tween_OutInCubic#(t#, startY#, endY# - startY#, 1)
;        currentZ# = Tween_OutInCubic#(t#, startZ#, endZ# - startZ#, 1)
;    Else
;        currentX# = Tween_OutInCubic#(1 - t#, startX#, endX# - startX#, 1)  ; Interpolation inverse
;        currentY# = Tween_OutInCubic#(1 - t#, startY#, endY# - startY#, 1)
;        currentZ# = Tween_OutInCubic#(1 - t#, startZ#, endZ# - startZ#, 1)
;    EndIf
	
    ; Calculer les positions du cube avec l'interpolation
    If isForward% = 1 Then
        currentX# = Tween_InOutQuad#(t#, startX#, endX# - startX#, 1)
        currentY# = Tween_InOutQuad#(t#, startY#, endY# - startY#, 1)
        currentZ# = Tween_InOutQuad#(t#, startZ#, endZ# - startZ#, 1)
    Else
        currentX# = Tween_Linear#(1 - t#, startX#, endX# - startX#, 1)  ; Interpolation inverse
        currentY# = Tween_Linear#(1 - t#, startY#, endY# - startY#, 1)
        currentZ# = Tween_Linear#(1 - t#, startZ#, endZ# - startZ#, 1)
    EndIf
	
	
    
    ; Déplacer le cube
    PositionEntity cube, currentX#, currentY#, currentZ#    
End Function

; ---------------------------------------------------------------------
; Linear : Représente une interpolation linéaire, où le mouvement entre
; deux valeurs est constant sur toute la durée de l'animation.
; ---------------------------------------------------------------------
Function Tween_Linear#(t#, b#, c#, d#)
    Return c# * t# / d# + b#
End Function
; ---------------------------------------------------------------------------
; InQuad : Est une courbe qui commence lentement et accélère progressivement.
; ---------------------------------------------------------------------------
Function Tween_InQuad#(t#, b#, c#, d#)
    Return c# * (t# / d#) ^ 2 + b#
End Function
; -----------------------------------------------------------------------------------------
; OutQuad : Est une courbe qui commence rapidement et ralentit progressivement vers la fin.
; -----------------------------------------------------------------------------------------
Function Tween_OutQuad#(t#, b#, c#, d#)
    t# = t# / d#
    Return -c# * t# * (t# - 2) + b#
End Function
; --------------------------------------------------------
; InOutQuad : Lent au début et à la fin, rapide au milieu.
; --------------------------------------------------------
Function Tween_InOutQuad#(t#, b#, c#, d#)
    t# = t# / d# * 2
    If t# < 1 Then
        Return c# / 2 * (t# ^ 2) + b#
    End If
    Return -c# / 2 * ((t# - 1) * (t# - 3) - 1) + b#
End Function
; --------------------------------------------------------
; OutInQuad : Rapide au début et à la fin, lent au milieu.
; --------------------------------------------------------
Function Tween_OutInQuad#(t#, b#, c#, d#)
    If t# < d# / 2 Then
        Return Tween_OutQuad#(t# * 2, b#, c# / 2, d#)
    End If
    Return Tween_InQuad#((t# * 2) - d#, b# + c# / 2, c# / 2, d#)
End Function
; -------------------------------------------------------------------------------
; InCubic : Est une courbe qui commence très lentement, puis accélère rapidement, 
; avec un changement de vitesse plus prononcé que dans les courbes quadratiques.
; -------------------------------------------------------------------------------
Function Tween_InCubic#(t#, b#, c#, d#)
    Return c# * (t# / d#) ^ 3 + b#
End Function
; ---------------------------------------------------------------------------
; OutCubic : Commence rapidement et ralentit progressivement à la fin. 
; OutCubic démarre avec une vitesse élevée puis se stabilise progressivement.
; ---------------------------------------------------------------------------
Function Tween_OutCubic#(t#, b#, c#, d#)
    Return c# * ((t# / d# - 1) ^ 3 + 1) + b#
End Function
; ---------------------------------------------------------------------------------
; InOutCubic : Combine les comportements des fonctions InCubic et OutCubic.
; Elle commence lentement, accélère au milieu, puis ralentit à nouveau vers la fin.
; ---------------------------------------------------------------------------------
Function Tween_InOutCubic#(t#, b#, c#, d#)
    t# = t# / d# * 2
    If t# < 1 Then
        Return c# / 2 * t# * t# * t# + b#
    End If
    t# = t# - 2
    Return c# / 2 * (t# * t# * t# + 2) + b#
End Function
; ---------------------------------------------------------------------------------------------------------
; OutInCubic : Combine les comportements des fonctions OutCubic et InCubic, mais dans un ordre inversé :
; elle commence par une animation qui ralentit (sortie) et puis accélère (entrée) après le milieu du cycle.
; ---------------------------------------------------------------------------------------------------------
Function Tween_OutInCubic#(t#, b#, c#, d#)
    If t# < d# / 2 Then
        Return Tween_OutCubic#(t# * 2, b#, c# / 2, d#)
    End If
    Return Tween_InCubic#((t# * 2) - d#, b# + c# / 2, c# / 2, d#)
End Function
